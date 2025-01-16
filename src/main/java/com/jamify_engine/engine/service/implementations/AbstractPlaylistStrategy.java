package com.jamify_engine.engine.service.implementations;

import com.jamify_engine.engine.models.dto.playlists.PlaylistEndJobVM;
import com.jamify_engine.engine.models.dto.playlists.SpotifyPlaylistDTO;
import com.jamify_engine.engine.service.interfaces.IPlaylistStrategy;

public abstract class AbstractPlaylistStrategy implements IPlaylistStrategy<SpotifyPlaylistDTO> {
    @Override
    public SpotifyPlaylistDTO createAndPopulatePlaylist(PlaylistEndJobVM endJobVM) {
        SpotifyPlaylistDTO playlistDTO = createPlaylistInAGivenUserAccount(
                endJobVM.getUserId(),
                "RANDOMPLAYLISTNAME",
                "RANDOMDESCRIPTION",
                false
        );

        return addMusicsToPlaylist(playlistDTO.getId(), endJobVM.getData().getMusics().stream().toList());
    }
}