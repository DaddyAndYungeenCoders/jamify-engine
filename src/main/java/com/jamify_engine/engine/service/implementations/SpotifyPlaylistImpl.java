package com.jamify_engine.engine.service.implementations;

import com.jamify_engine.engine.config.webClient.SpotifyWebClient;
import com.jamify_engine.engine.models.dto.playlists.SpotifyPlaylistDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
public class SpotifyPlaylistImpl extends AbstractPlaylistStrategy {
    private final WebClient webClient;

    @Autowired
    public SpotifyPlaylistImpl(@Qualifier("spotifyServiceWebClient") WebClient spotifyWebClient) {
        this.webClient = spotifyWebClient;
    }

    @Override
    public SpotifyPlaylistDTO createPlaylistInAGivenUserAccount(Long jamifyUserId, String playlistName, String playlistDescription, boolean ispublic) {
        // TODO
        return null;
    }

    @Override
    public SpotifyPlaylistDTO addMusicsToPlaylist(String playlistId, List<Long> musics) {
        // TODO
        return null;
    }
}
