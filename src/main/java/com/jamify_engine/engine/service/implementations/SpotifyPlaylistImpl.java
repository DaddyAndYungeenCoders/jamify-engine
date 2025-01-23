package com.jamify_engine.engine.service.implementations;

import com.jamify_engine.engine.exceptions.external.MaxRetriesExceededException;
import com.jamify_engine.engine.exceptions.musics.MusicNotFoundException;
import com.jamify_engine.engine.exceptions.security.UnauthorizedException;
import com.jamify_engine.engine.models.dto.external.spotify.SpotifySearchResultDTO;
import com.jamify_engine.engine.models.dto.playlists.SpotifyPlaylistDTO;
import com.jamify_engine.engine.models.entities.UserEntity;
import com.jamify_engine.engine.models.enums.ProvidersEnum;
import com.jamify_engine.engine.models.vms.PlaylistRequest;
import com.jamify_engine.engine.service.interfaces.MusicService;
import com.jamify_engine.engine.service.interfaces.UserAccessTokenService;
import com.jamify_engine.engine.service.interfaces.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SpotifyPlaylistImpl extends AbstractPlaylistStrategy<SpotifyPlaylistDTO> {
    private final WebClient webClient;
    private final MusicService musicService;
    private final static int MAX_RETRIES = 1;

    @Autowired
    public SpotifyPlaylistImpl(@Qualifier("spotifyServiceWebClient") WebClient spotifyWebClient,
                               UserAccessTokenService userAccessTokenService,
                               UserService userService,
                               MusicService musicService) {
        super(userAccessTokenService, ProvidersEnum.SPOTIFY.getProvider(), userService);
        this.webClient = spotifyWebClient;
        this.musicService = musicService;
    }

    @Override
    public SpotifyPlaylistDTO createPlaylistInAGivenUserAccount(String userProviderId, String playlistName, String playlistDescription, boolean ispublic) {
        UserEntity givenUserAccount = getGivenUser(userProviderId);
        String spotifyAccessToken = getProviderAccessToken(userProviderId);
        return specificPlaylistCreation(givenUserAccount.getUserProviderId(), spotifyAccessToken, playlistName, playlistDescription, ispublic, userProviderId);
    }

    @Override
    public SpotifyPlaylistDTO addMusicsToPlaylist(String playlistId, List<Long> musics, String providerAccessToken) {
        String uri = "/playlists/%s/tracks".formatted(playlistId);

        List<String> musics_uri = retrieveUrisFromMusicIds(musics, providerAccessToken);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("uris", musics_uri);
        requestBody.put("position", 0);

        return this.webClient
                .post()
                .uri(uri)
                .header(HttpHeaders.AUTHORIZATION, "Bearer %s".formatted(providerAccessToken))
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(SpotifyPlaylistDTO.class)
                .doOnSuccess(response -> log.info("Musics have been added in the playlist {} through Spotify, response was {}", playlistId, response))
                .doOnError(exception -> log.error("Error while trying to add musics to a playlist through Spotify, message was: \n {}", exception.getMessage()))
                .block();
    }

    @Override
    public String getProviderName() {
        return ProvidersEnum.SPOTIFY.getProvider();
    }

    @Override
    protected SpotifyPlaylistDTO specificPlaylistCreation(String providerUsername, String providerAccessToken, String playlistName, String playlistDescription, boolean ispublic, String userProviderId) {
        String uri = "/users/%s/playlists".formatted(providerUsername);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("name", playlistName);
        requestBody.put("description", playlistDescription);
        requestBody.put("public", ispublic);

        PlaylistRequest request = new PlaylistRequest(
                uri,
                providerAccessToken,
                userProviderId,
                providerUsername,
                requestBody
        );

        return createPlaylist(request).block(); // FIXME shall we delete the block?
    }

    public Mono<SpotifyPlaylistDTO> createPlaylist(PlaylistRequest request) {
        return createPlaylistWithRetry(request, 0);
    }

    private Mono<SpotifyPlaylistDTO> createPlaylistWithRetry(PlaylistRequest request, int retryCount) {
        return this.webClient
                .post()
                .uri(request.getUri())
                .header(HttpHeaders.AUTHORIZATION, "Bearer %s".formatted(request.getProviderAccessToken()))
                .bodyValue(request.getRequestBody())
                .retrieve()
                .onStatus(
                        HttpStatus.UNAUTHORIZED::equals,
                        response -> {
                            if (retryCount >= MAX_RETRIES) {
                                return Mono.error(new MaxRetriesExceededException("Max retries exceeded after token refresh"));
                            }
                            log.info("Received 401, attempting token refresh and retry");
                            return Mono.error(new UnauthorizedException("401 Unauthorized"));
                        }
                )
                .bodyToMono(SpotifyPlaylistDTO.class)
                .onErrorResume(UnauthorizedException.class, exception -> handleTokenRefresh(request, retryCount + 1))
                .doOnSuccess(response -> log.info("Playlist created successfully for user {}", request.getUsername()))
                .doOnError(exception -> log.error("Error creating playlist: {}", exception.getMessage()));
    }

    private Mono<SpotifyPlaylistDTO> handleTokenRefresh(PlaylistRequest request, int retryCount) {
        return Mono.fromCallable(() ->
                        refreshToken(request.getUserProviderId())
                )
                .flatMap(newToken -> {
                    PlaylistRequest newRequest = request.withProviderAccessToken(newToken);
                    return createPlaylistWithRetry(newRequest, retryCount + 1);
                });
    }

    /**
     * retourner le token rafraichi
     * @param userProviderId
     * @return
     */
    private String refreshToken(String userProviderId) {
        UserEntity user = getGivenUser(userProviderId);
        return userAccessTokenService.refreshAccessToken(user.getId(), this.getProviderName());
    }

    private List<String> retrieveUrisFromMusicIds(List<Long> musics, String providerAccessToken) {
        return retrieveMusicIdFromISRC(musics.stream().map(
                id -> musicService.findById(id).isrc()
        ).toList(), providerAccessToken);
    }

    private List<String> retrieveMusicIdFromISRC(List<String> isrcs, String providerAccessToken) {
        return isrcs.stream()
                .map(isrc -> {
                    try {
                        return getSpotifyUriFromISRC(isrc, providerAccessToken).getTracks().getItem().getUri();
                    } catch (MusicNotFoundException ex) {
                        log.error("There was an error while trying to find the music with isrc {}", isrc);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private SpotifySearchResultDTO getSpotifyUriFromISRC(String isrc, String providerAccessToken) {
        String uri = "/search?type=track&q=isrc:%s&market=FR&limit=1&offset=0".formatted(isrc);
        return this.webClient
                .get()
                .uri(uri)
                .header(HttpHeaders.AUTHORIZATION, "Bearer %s".formatted(providerAccessToken))
                .retrieve()
                .bodyToMono(SpotifySearchResultDTO.class)
                .doOnSuccess(response -> log.debug("spotify uri for isrc {} is {}", isrc, response))
                .doOnError(exception -> {
                    throw new MusicNotFoundException(exception.getMessage());
                })
                .block();
    }
}
