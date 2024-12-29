package com.jamify_engine.engine.service.implementations;

import com.jamify_engine.engine.models.dto.JamDTO;
import com.jamify_engine.engine.models.dto.MusicDTO;
import com.jamify_engine.engine.service.interfaces.IJamStrategy;
import jdk.jshell.spi.ExecutionControl;

import java.util.List;

public abstract class JamStrategy implements IJamStrategy {
    @Override
    public void playMusic(Long musicId, Long jamId) throws ExecutionControl.NotImplementedException {
        throw new ExecutionControl.NotImplementedException("not yet");
    }

    @Override
    public List<MusicDTO> getAllInQueue(Long jamId) throws ExecutionControl.NotImplementedException {
        throw new ExecutionControl.NotImplementedException("not yet");
    }

    @Override
    public void joinJam(Long jamId) throws ExecutionControl.NotImplementedException {
        throw new ExecutionControl.NotImplementedException("not yet");
    }

    @Override
    public void leaveJam(Long jamId) throws ExecutionControl.NotImplementedException {
        throw new ExecutionControl.NotImplementedException("not yet");
    }

    @Override
    public JamDTO create(JamDTO entityToCreate) throws ExecutionControl.NotImplementedException {
        throw new ExecutionControl.NotImplementedException("not yet");
    }

    @Override
    public JamDTO update(Long id, JamDTO entityToUpdate) throws ExecutionControl.NotImplementedException {
        throw new ExecutionControl.NotImplementedException("not yet");
    }

    @Override
    public void delete(Long entityToDeleteId) throws ExecutionControl.NotImplementedException {
        throw new ExecutionControl.NotImplementedException("not yet");
    }

    @Override
    public JamDTO findById(Long entityId) throws ExecutionControl.NotImplementedException {
        throw new ExecutionControl.NotImplementedException("not yet");
    }

    @Override
    public List<JamDTO> findAll() throws ExecutionControl.NotImplementedException {
        throw new ExecutionControl.NotImplementedException("not yet");
    }

    protected abstract void specificPlay();
}
