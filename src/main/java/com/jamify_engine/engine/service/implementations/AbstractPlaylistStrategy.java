package com.jamify_engine.engine.service.implementations;

import com.jamify_engine.engine.models.dto.UserDTO;
import com.jamify_engine.engine.models.dto.playlists.PlaylistDTO;
import com.jamify_engine.engine.models.dto.playlists.PlaylistEndJobVM;
import com.jamify_engine.engine.models.entities.PlaylistEntity;
import com.jamify_engine.engine.models.entities.UserEntity;
import com.jamify_engine.engine.models.mappers.PlaylistMapper;
import com.jamify_engine.engine.repository.PlaylistRepository;
import com.jamify_engine.engine.service.interfaces.IPlaylistStrategy;
import com.jamify_engine.engine.service.interfaces.UserAccessTokenService;
import com.jamify_engine.engine.service.interfaces.UserService;
import reactor.core.publisher.Mono;

public abstract class AbstractPlaylistStrategy<D extends PlaylistDTO> implements IPlaylistStrategy<D> {
    protected final UserAccessTokenService userAccessTokenService;
    protected final UserService userService;
    protected String provider;
    protected PlaylistRepository playlistRepository;
    protected PlaylistMapper<D, PlaylistEntity> playlistMapper;

    public AbstractPlaylistStrategy(UserAccessTokenService accessTokenService,
                                    String provider,
                                    UserService userService,
                                    PlaylistRepository playlistRepository,
                                    PlaylistMapper<D, PlaylistEntity> playlistMapper) {
        this.userAccessTokenService = accessTokenService;
        this.provider = provider;
        this.userService = userService;
        this.playlistMapper = playlistMapper;
    }

    @Override
    public D createAndPopulatePlaylist(PlaylistEndJobVM endJobVM) {
        D playlistDTO = createPlaylistInAGivenUserAccount(
                endJobVM.getUserId(),
                endJobVM.getData().getPlaylistName(),
                endJobVM.getData().getPlaylistDescription(),
                false
        );

        String providerAccessToken = getProviderAccessToken(endJobVM.getUserId());

        return addMusicsToPlaylist(
                playlistDTO.getId(),
                endJobVM.getData().getMusics().stream().toList(),
                providerAccessToken
        ).flatMap(
                result -> Mono.just(this.playlistMapper.toDTO(persistPlaylist(result)))
        ).onErrorMap(
                error -> new IllegalArgumentException("Uncaught exception while adding musics to playlist.")
        ).block();
    }

    protected UserEntity getGivenUser(Long jamifyUserId) {
        UserDTO user = userService.findById(jamifyUserId);

        return userService.findEntityByEmail(user.email());
    }

    protected String getProviderAccessToken(Long jamifyUserId) {
        UserEntity givenUserAccount = getGivenUser(jamifyUserId);

        return this.userAccessTokenService.getAccessToken(
                givenUserAccount.getEmail(), this.provider
        );
    }


    protected abstract D specificPlaylistCreation(String providerUsername, String providerAccessToken, String playlistName, String playlistDescription, boolean ispublic, Long jamifyUserId);
}