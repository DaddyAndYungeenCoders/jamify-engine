package com.jamify_engine.engine.service.interfaces;

import com.jamify_engine.engine.models.dto.JamDTO;
import com.jamify_engine.engine.models.dto.MusicDTO;
import com.jamify_engine.engine.models.entities.JamEntity;
import com.jamify_engine.engine.models.vms.JamInstantLaunching;
import com.jamify_engine.engine.service.ServiceBasics;
import jdk.jshell.spi.ExecutionControl;

import java.util.List;

public interface IJamStrategy extends ServiceBasics<JamDTO> {
    void playMusic(Long musicId, Long jamId) throws ExecutionControl.NotImplementedException;
    void playMusic();
    List<MusicDTO> getAllInQueue(Long jamId) throws ExecutionControl.NotImplementedException;
    void joinJam(Long jamId);
    void leaveJam(Long jamId);
    JamDTO launchAJam(JamInstantLaunching jamVM);
    JamEntity findRunningJamForUser();
    void stopAJam(Long jamId);
    List<JamDTO> findAllRunningJams();
}
