package com.jamify_engine.engine.controllers.playlist;

import com.jamify_engine.engine.models.dto.playlists.PlaylistDTO;
import com.jamify_engine.engine.models.dto.playlists.PlaylistEndJobVM;
import com.jamify_engine.engine.service.factories.PlaylistStrategyFactory;
import com.jamify_engine.engine.service.interfaces.IPlaylistStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/playlist")
@Slf4j
public class PlaylistController {
    private final PlaylistStrategyFactory strategyFactory;

    @Autowired
    public PlaylistController(PlaylistStrategyFactory strategyFactory) {
        this.strategyFactory = strategyFactory;
    }

    @PostMapping("/generated")
    ResponseEntity<PlaylistDTO> persistGeneratedPlaylist(@RequestBody PlaylistEndJobVM demand) {
        log.info("Asking to the orchestrator for a playlist generation job...");
        // TODO choose more precisely the provider
        IPlaylistStrategy service = strategyFactory.getStrategy("spotify");
        return ResponseEntity.ok(service.createAndPopulatePlaylist(demand));
    }
}
