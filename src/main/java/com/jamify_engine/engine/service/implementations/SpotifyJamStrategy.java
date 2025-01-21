package com.jamify_engine.engine.service.implementations;

import com.jamify_engine.engine.models.dto.JamDTO;
import com.jamify_engine.engine.models.dto.MusicDTO;
import com.jamify_engine.engine.models.entities.UserEntity;
import com.jamify_engine.engine.models.enums.ProvidersEnum;
import com.jamify_engine.engine.models.mappers.JamMapper;
import com.jamify_engine.engine.models.mappers.MusicMapper;
import com.jamify_engine.engine.repository.JamRepository;
import com.jamify_engine.engine.service.interfaces.MusicService;
import com.jamify_engine.engine.service.interfaces.TagsService;
import com.jamify_engine.engine.service.interfaces.UserAccessTokenService;
import com.jamify_engine.engine.service.interfaces.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashSet;
import java.util.Set;

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
                              MusicMapper musicMapper) {
        super(userService, jamRepository, jamMapper, musicService, tagsService, musicMapper);
        spotifyWebClient = spotifyWebClient1;
        userAccessTokenService = userAccessTokenService1;
    }

    @Override
    protected void specificPlay(MusicDTO music, JamDTO jam) {
        log.debug("Playing a music for all jams participants");
        Set<String> accessTokens = new HashSet<>();

        for (UserEntity user: getAllUsersInAJam(jam)) {
            accessTokens.add(userAccessTokenService.getAccessToken(user.getEmail(), ProvidersEnum.SPOTIFY.getProvider()));
        }

        for (String accessToken: accessTokens) {
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
}
