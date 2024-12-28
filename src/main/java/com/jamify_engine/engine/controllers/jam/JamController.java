package com.jamify_engine.engine.controllers.jam;

import com.jamify_engine.engine.controllers.CRUDController;
import com.jamify_engine.engine.models.dto.JamDTO;
import com.jamify_engine.engine.models.dto.MusicDTO;
import com.jamify_engine.engine.service.interfaces.IJamStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/jams")
@Slf4j
public class JamController extends CRUDController<JamDTO, IJamStrategy> {
    IJamStrategy jamStrategy;

    /**
     * @param jamId the jam the user wants to join
     * @param userId
     * FIXME remove userId from parameters and use the one in headers
     */
    @PutMapping("/join")
    void join(final Long jamId, final Long userId) {
        log.info("[REST CALL] - User with id {}, Joining jam with id {}", jamId, userId);
        jamStrategy.joinJam(userId, jamId);
    }

    /**
     * @param jamId the jam the user wants to leave
     * @param userId
     * FIXME remove userId from parameters and use the one in headers
     */
    @PutMapping("/leave")
    void leave(final Long jamId, final Long userId) {
        log.info("[REST CALL] - User with id {}, Leaving jam with id {}", jamId, userId);
        jamStrategy.leaveJam(userId, jamId);
    }

    /**
     * @return Every queued musics in a given jam
     */
    @GetMapping("/queue/{jamId}")
    List<MusicDTO> getAllQueuedMusic(@PathVariable final Long jamId) {
        log.info("[REST CALL] - Getting all queued musics in jam with id {}", jamId);
        return jamStrategy.getAllInQueue(jamId);
    }

    @PostMapping("/play/{musicId}/{jamId}")
    void playMusic(@PathVariable final Long musicId, @PathVariable final Long jamId) {
        log.info("[REST CALL] - Playing the music with id {} for every user in jam with id {}", musicId, jamId);
        service.playMusic(musicId, jamId);
    }
}
