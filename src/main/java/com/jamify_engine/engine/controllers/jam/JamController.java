package com.jamify_engine.engine.controllers.jam;

import com.jamify_engine.engine.controllers.CRUDController;
import com.jamify_engine.engine.models.dto.JamDTO;
import com.jamify_engine.engine.models.dto.MusicDTO;
import com.jamify_engine.engine.models.vms.JamInstantLaunching;
import com.jamify_engine.engine.service.interfaces.IJamStrategy;
import jdk.jshell.spi.ExecutionControl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/jams")
@Slf4j
public class JamController extends CRUDController<JamDTO, IJamStrategy> {
    private final IJamStrategy jamStrategy;

    @Autowired
    public JamController(IJamStrategy jamStrategy) {
        this.service = jamStrategy;
        this.jamStrategy = jamStrategy;
    }

    /**
     * @param jamId the jam the connected user wants to join
     */
    @PutMapping("/join/{jamId}")
    void join(@PathVariable("jamId") Long jamId) throws ExecutionControl.NotImplementedException {
        log.info("[REST CALL] - User Joining jam with id {}", jamId);
        jamStrategy.joinJam(jamId);
    }

    /**
     * @param jamId the jam the connected user wants to leave
     */
    @PutMapping("/leave/{jamId}")
    void leave(@PathVariable Long jamId) throws ExecutionControl.NotImplementedException {
        log.info("[REST CALL] - User Leaving jam with id {}", jamId);
        jamStrategy.leaveJam(jamId);
    }

    /**
     * @return Every queued musics in a given jam
     */
    @GetMapping("/queue/{jamId}")
    ResponseEntity<List<MusicDTO>> getAllQueuedMusic(@PathVariable final Long jamId) throws ExecutionControl.NotImplementedException {
        log.info("[REST CALL] - Getting all queued musics in jam with id {}", jamId);
        return ResponseEntity.ok(jamStrategy.getAllInQueue(jamId));
    }

    /**
     * Play a music in a jam
     *
     * @param musicId the music we want to broadcast
     * @param jamId   the jam we want to play the music 'in'
     * @throws ExecutionControl.NotImplementedException
     */
    @PostMapping("/play/{musicId}/{jamId}")
    void playMusic(@PathVariable final Long musicId, @PathVariable final Long jamId) throws ExecutionControl.NotImplementedException {
        log.info("[REST CALL] - Playing the music with id {} for every user in jam with id {}", musicId, jamId);
        jamStrategy.playMusic(musicId, jamId);
    }

    /**
     * Launch a jam.
     * Makes a jam joinable by updating its status and the host status.
     * @return The launched JamDTO
     */
    @PostMapping("/launch")
    ResponseEntity<JamDTO> launchAJam(@RequestBody JamInstantLaunching jamVM) {
        log.info("[REST CALL] - launching a jam...");
        return ResponseEntity.ok(jamStrategy.launchAJam(jamVM));
    }

    /**
     * Stop a running jam.
     *
     * @return The launched JamDTO
     */
    @PostMapping("/stop/{jamId}")
    ResponseEntity<Boolean> stopAJam(@PathVariable Long jamId) {
        log.info("[REST CALL] - launching a jam...");
        jamStrategy.stopAJam(jamId);
        return ResponseEntity.ok(true);
    }
}
