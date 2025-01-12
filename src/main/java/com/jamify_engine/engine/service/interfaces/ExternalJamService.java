package com.jamify_engine.engine.service.interfaces;

import com.jamify_engine.engine.models.dto.external.spotify.SpotifyTrackDTO;

public interface ExternalJamService {
    SpotifyTrackDTO checkUserTrack(Long userId);
}