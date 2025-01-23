package com.jamify_engine.engine.service.interfaces;

import com.jamify_engine.engine.models.dto.playlists.PlaylistDTO;
import com.jamify_engine.engine.models.dto.playlists.PlaylistEndJobVM;

import java.util.List;

public interface IPlaylistStrategy<D extends PlaylistDTO> {
    D createAndPopulatePlaylist(PlaylistEndJobVM endJobVM);

    D createPlaylistInAGivenUserAccount(String userProviderId, String playlistName, String playlistDescription, boolean ispublic);

    D addMusicsToPlaylist(String playlistId, List<Long> musics, String providerAccessToken);

    String getProviderName();
}
