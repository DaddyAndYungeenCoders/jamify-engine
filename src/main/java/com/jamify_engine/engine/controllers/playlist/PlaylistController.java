package com.jamify_engine.engine.controllers.playlist;

import com.jamify_engine.engine.exceptions.security.InvalidApiKeyException;
import com.jamify_engine.engine.models.dto.playlists.PlaylistDTO;
import com.jamify_engine.engine.models.dto.playlists.PlaylistEndJobVM;
import com.jamify_engine.engine.service.factories.PlaylistStrategyFactory;
import com.jamify_engine.engine.service.interfaces.IPlaylistStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/playlist")
@Slf4j
public class PlaylistController {
    @Value("${config.uaa.jamify-engine-api-key}")
    private String jamifyEngineApiKey;

    private final PlaylistStrategyFactory strategyFactory;

    @Autowired
    public PlaylistController(PlaylistStrategyFactory strategyFactory) {
        this.strategyFactory = strategyFactory;
    }

    @PostMapping("/generated")
    ResponseEntity<PlaylistDTO> persistGeneratedPlaylist(@RequestBody PlaylistEndJobVM demand, @RequestHeader(value = "X-API-KEY") String apiKey) {
        log.info("Asking to the orchestrator for a playlist generation job...");
        if (!apiKey.equals(jamifyEngineApiKey)) {
            throw new InvalidApiKeyException("Invalid API Key");
        }
        IPlaylistStrategy service = strategyFactory.getStrategy("spotify");
        return ResponseEntity.ok(service.createAndPopulatePlaylist(demand));
    }
}
