package com.jamify_engine.engine.controllers.jam;

import com.jamify_engine.engine.controllers.CRUDController;
import com.jamify_engine.engine.models.dto.JamDTO;
import com.jamify_engine.engine.models.dto.MusicDTO;
import com.jamify_engine.engine.models.vms.JamInstantLaunching;
import com.jamify_engine.engine.service.interfaces.IJamStrategy;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jdk.jshell.spi.ExecutionControl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/jams")
@Slf4j
@Tag(name = "Jam Management", description = "Endpoints for managing jam sessions")
public class JamController extends CRUDController<JamDTO, IJamStrategy> {
    @Autowired
    public JamController(IJamStrategy jamStrategy) {
        this.service = jamStrategy;
    }

    @Operation(
            summary = "Join a jam session",
            description = "Allows the connected user to join a specific jam session"
    )
    @PutMapping("/join/{jamId}")
    void join(@Parameter(description = "ID of the jam to join") @PathVariable("jamId") Long jamId)
            throws ExecutionControl.NotImplementedException {
        log.info("[REST CALL] - User Joining jam with id {}", jamId);
        service.joinJam(jamId);
    }

    @Operation(
        summary = "Get all running jams in the database",
            description = "Get all jams in the database with status set to running."
    )
    @GetMapping("/running")
    ResponseEntity<Set<JamDTO>> getAllRunningJam() {
        return ResponseEntity.ok(new HashSet<>(service.findAllRunningJams()));
    }

    @Operation(
            summary = "Leave a jam session",
            description = "Allows the connected user to leave a specific jam session"
    )
    @PutMapping("/leave/{jamId}")
    void leave(@Parameter(description = "ID of the jam to leave") @PathVariable Long jamId)
            throws ExecutionControl.NotImplementedException {
        log.info("[REST CALL] - User Leaving jam with id {}", jamId);
        service.leaveJam(jamId);
    }

    @Operation(
            summary = "Get queued music in a jam",
            description = "Retrieves all queued music tracks in a specific jam session",
            responses = {
                    @ApiResponse(responseCode = "200", description = "List of queued music successfully retrieved")
            }
    )
    @GetMapping("/queue/{jamId}")
    ResponseEntity<List<MusicDTO>> getAllQueuedMusic(
            @Parameter(description = "ID of the jam to get the queue from")
            @PathVariable final Long jamId
    ) throws ExecutionControl.NotImplementedException {
        log.info("[REST CALL] - Getting all queued musics in jam with id {}", jamId);
        return ResponseEntity.ok(service.getAllInQueue(jamId));
    }

    @Operation(
            summary = "Play music in a jam",
            description = "Broadcasts a specific music track in a jam session",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Music successfully started playing")
            }
    )
    @PostMapping("/play/{musicId}/{jamId}")
    ResponseEntity<Boolean> playMusic(
            @Parameter(description = "ID of the music to play") @PathVariable final Long musicId,
            @Parameter(description = "ID of the jam to play the music in") @PathVariable final Long jamId
    ) throws ExecutionControl.NotImplementedException {
        log.info("[REST CALL] - Playing the music with id {} for every user in jam with id {}", musicId, jamId);
        service.playMusic(musicId, jamId);
        return ResponseEntity.ok(true);
    }

    @Operation(
            summary = "Launch a jam session",
            description = "Makes a jam joinable by updating its status and the host status",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Jam successfully launched")
            }
    )
    @PostMapping("/launch")
    ResponseEntity<JamDTO> launchAJam(@RequestBody JamInstantLaunching jamVM) {
        log.info("[REST CALL] - launching a jam...");
        return ResponseEntity.ok(service.launchAJam(jamVM));
    }

    @Operation(
            summary = "Stop a jam session",
            description = "Stops a running jam session",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Jam successfully stopped")
            }
    )
    @PostMapping("/stop/{jamId}")
    ResponseEntity<Boolean> stopAJam(
            @Parameter(description = "ID of the jam to stop")
            @PathVariable Long jamId
    ) {
        log.info("[REST CALL] - launching a jam...");
        service.stopAJam(jamId);
        return ResponseEntity.ok(true);
    }
}