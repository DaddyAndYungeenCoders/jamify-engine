package com.jamify_engine.engine.controllers.jam;

import com.jamify_engine.engine.controllers.CRUDController;
import com.jamify_engine.engine.models.dto.JamDTO;
import com.jamify_engine.engine.models.dto.MusicDTO;
import com.jamify_engine.engine.service.interfaces.IJamStrategy;
import jdk.jshell.spi.ExecutionControl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/jams")
@Slf4j
public class JamController extends CRUDController<JamDTO, IJamStrategy> {
    IJamStrategy jamStrategy;

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
        log.info("[REST CALL] - User Leaving jam with id {}", jamId );
        jamStrategy.leaveJam(jamId);
    }

    /**
     * @return Every queued musics in a given jam
     */
    @GetMapping("/queue/{jamId}")
    List<MusicDTO> getAllQueuedMusic(@PathVariable final Long jamId) throws ExecutionControl.NotImplementedException {
        log.info("[REST CALL] - Getting all queued musics in jam with id {}", jamId);
        return jamStrategy.getAllInQueue(jamId);
    }

    @PostMapping("/play/{musicId}/{jamId}")
    void playMusic(@PathVariable final Long musicId, @PathVariable final Long jamId) throws ExecutionControl.NotImplementedException {
        log.info("[REST CALL] - Playing the music with id {} for every user in jam with id {}", musicId, jamId);
        service.playMusic(musicId, jamId);
    }
}
