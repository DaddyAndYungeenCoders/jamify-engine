package com.jamify_engine.engine.service.interfaces;

import com.jamify_engine.engine.models.dto.playlists.PlaylistDTO;
import com.jamify_engine.engine.models.dto.playlists.PlaylistEndJobVM;
import com.jamify_engine.engine.models.entities.PlaylistEntity;
import com.jamify_engine.engine.models.entities.UserEntity;
import reactor.core.publisher.Mono;

import java.util.List;

public interface IPlaylistStrategy<D extends PlaylistDTO> {
    D createAndPopulatePlaylist(PlaylistEndJobVM endJobVM);

    D createPlaylistInAGivenUserAccount(Long jamifyUserId, String playlistName, String playlistDescription, boolean ispublic);

    Mono<D> addMusicsToPlaylist(String playlistId, List<Long> musics, String providerAccessToken);

    PlaylistEntity persistPlaylist(D playlistDTO);

    String getProviderName();
}
