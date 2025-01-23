package com.jamify_engine.engine.service.implementations;

import com.jamify_engine.engine.exceptions.common.BadRequestException;
import com.jamify_engine.engine.models.dto.JamDTO;
import com.jamify_engine.engine.models.dto.MusicDTO;
import com.jamify_engine.engine.models.dto.external.spotify.SpotifySearchFullRequestResponse;
import com.jamify_engine.engine.models.entities.JamEntity;
import com.jamify_engine.engine.models.entities.UserEntity;
import com.jamify_engine.engine.models.enums.ProvidersEnum;
import com.jamify_engine.engine.models.mappers.JamMapper;
import com.jamify_engine.engine.models.mappers.MusicMapper;
import com.jamify_engine.engine.repository.JamRepository;
import com.jamify_engine.engine.service.interfaces.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;

@Service
@Slf4j
public class SpotifyJamStrategy extends JamStrategy {
    private final WebClient spotifyWebClient;
    private final UserAccessTokenService userAccessTokenService;

    @Autowired
    public SpotifyJamStrategy(UserService userService,
                              JamRepository jamRepository,
                              JamMapper jamMapper,
                              MusicService musicService,
                              @Qualifier("spotifyServiceWebClient") WebClient spotifyWebClient1,
                              UserAccessTokenService userAccessTokenService1,
                              TagsService tagsService,
                              MusicMapper musicMapper,
                              ParticipantService participantService1) {
        super(userService, jamRepository, jamMapper, musicService, tagsService, musicMapper, participantService1);
        spotifyWebClient = spotifyWebClient1;
        userAccessTokenService = userAccessTokenService1;
    }

    @Override
    protected void specificPlay(MusicDTO music, JamDTO jam) {
        log.debug("Playing a music for all jams participants");
        Set<String> accessTokens = new HashSet<>();

        for (UserEntity user : getAllUsersInAJam(jam)) {
            accessTokens.add(userAccessTokenService.getAccessToken(user.getEmail(), ProvidersEnum.SPOTIFY.getProvider()));
        }

        for (String accessToken : accessTokens) {
            spotifyWebClient.post()
                    .uri("/me/player/play")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer %s".formatted(accessToken))
                    .retrieve()
                    .toBodilessEntity()
                    .doOnError(response -> log.error("SPOTIFY WEB CLIENT ERROR: {}", response))
                    .doOnSuccess(response -> log.info("SPOTIFY WEB CLIENT SUCCESS -> launched a music {}", response))
                    .block();
        }
    }

    @Override
    protected void specificPlay(UserEntity host, JamEntity jam) {
        String hostAccessToken = userAccessTokenService.getAccessToken(host.getEmail(), ProvidersEnum.SPOTIFY.getProvider());
        String getCurrentlyPlayingSongForGivenUserUri = "/me/player/currently-playing";
        SpotifySearchFullRequestResponse currentlyPlayingSong = spotifyWebClient.get()
                .uri(getCurrentlyPlayingSongForGivenUserUri)
                .header(HttpHeaders.AUTHORIZATION, "Bearer %s".formatted(hostAccessToken))
                .retrieve()
                .bodyToMono(SpotifySearchFullRequestResponse.class)
                .doOnError(response -> log.error("SPOTIFY WEB CLIENT ERROR: {}", response.getMessage()))
                .doOnSuccess(response -> log.info("SPOTIFY WEB CLIENT SUCCESS -> launched a music {}", response))
                .block();

        if (currentlyPlayingSong == null) {
            throw new BadRequestException("The requested song could not be found by spotify API");
        }

        if (currentlyPlayingSong.getItem() == null) {
            throw new BadRequestException("The requested song could not be found by spotify API");
        }

        String spotifyUri = currentlyPlayingSong.getItem().getUri();

        Set<String> accessTokens = new HashSet<>();
        for (UserEntity user : getAllUsersInAJam(jam)) {
            accessTokens.add(userAccessTokenService.getAccessToken(user.getEmail(), ProvidersEnum.SPOTIFY.getProvider()));
        }

        for (String accessToken: accessTokens) {
            playMusicFromSpotifyUri(spotifyUri, accessToken);
        }
    }

    protected void playMusicFromSpotifyUri(String spotifyUri, String accessToken) {
        String targetUri = "/me/player/play";

        HashMap<String, Object> body = new HashMap<>();
        HashMap<String, Object> offset = new HashMap<>();
        offset.put("position", 0);

        body.put("uris", Collections.singletonList(spotifyUri));
        body.put("offset", offset);
        body.put("position_ms", 0);

        spotifyWebClient.put()
                .uri(targetUri)
                .header(HttpHeaders.AUTHORIZATION, "Bearer %s".formatted(accessToken))
                .bodyValue(body)
                .retrieve()
                .toBodilessEntity()
                .doOnError(response -> log.error("SPOTIFY WEB CLIENT ERROR: {}", response))
                .doOnSuccess(response -> log.info("SPOTIFY WEB CLIENT SUCCESS -> launched a music {}", response))
                .block();
    }
}
