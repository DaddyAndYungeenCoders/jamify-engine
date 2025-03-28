package com.jamify_engine.engine.service.implementations;

import com.jamify_engine.engine.models.dto.UserDTO;
import com.jamify_engine.engine.models.dto.playlists.PlaylistDTO;
import com.jamify_engine.engine.models.dto.playlists.PlaylistEndJobVM;
import com.jamify_engine.engine.models.entities.UserEntity;
import com.jamify_engine.engine.service.interfaces.IPlaylistStrategy;
import com.jamify_engine.engine.service.interfaces.UserAccessTokenService;
import com.jamify_engine.engine.service.interfaces.UserService;

public abstract class AbstractPlaylistStrategy<D extends PlaylistDTO> implements IPlaylistStrategy<D> {
    protected final UserAccessTokenService userAccessTokenService;
    protected final UserService userService;
    protected String provider;

    public AbstractPlaylistStrategy(UserAccessTokenService accessTokenService, String provider, UserService userService) {
        this.userAccessTokenService = accessTokenService;
        this.provider = provider;
        this.userService = userService;
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

        return addMusicsToPlaylist(playlistDTO.getId(), endJobVM.getData().getMusics().stream().toList(), providerAccessToken);
    }

    protected UserEntity getGivenUser(String userProviderId) {
        UserDTO user = userService.findByUserProviderId(userProviderId);

        return userService.findEntityByEmail(user.email());
    }

    protected String getProviderAccessToken(String userProviderId) {
        UserEntity givenUserAccount = getGivenUser(userProviderId);

        return this.userAccessTokenService.getAccessToken(
                givenUserAccount.getEmail(), this.provider
        );
    }


    protected abstract D specificPlaylistCreation(String providerUsername, String providerAccessToken, String playlistName, String playlistDescription, boolean ispublic, String userProviderId);
}