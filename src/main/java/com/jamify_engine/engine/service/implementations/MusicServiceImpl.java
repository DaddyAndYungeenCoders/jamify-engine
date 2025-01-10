package com.jamify_engine.engine.service.implementations;

import com.jamify_engine.engine.exceptions.musics.MusicNotFoundException;
import com.jamify_engine.engine.models.dto.MusicDTO;
import com.jamify_engine.engine.models.entities.MusicEntity;
import com.jamify_engine.engine.models.mappers.MusicMapper;
import com.jamify_engine.engine.repository.MusicRepository;
import com.jamify_engine.engine.service.interfaces.MusicService;
import jdk.jshell.spi.ExecutionControl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class MusicServiceImpl implements MusicService {
    private final MusicRepository musicRepository;
    private final MusicMapper musicMapper;

    @Override
    public MusicDTO create(MusicDTO entityToCreate) throws ExecutionControl.NotImplementedException {
        return null;
    }

    @Override
    public MusicDTO update(Long id, MusicDTO entityToUpdate) throws ExecutionControl.NotImplementedException {
        return null;
    }

    @Override
    public void delete(Long entityToDeleteId) throws ExecutionControl.NotImplementedException {

    }

    @Override
    public MusicDTO findById(Long entityId) throws ExecutionControl.NotImplementedException {
        return musicMapper.toDTO(
                    musicRepository.findById(entityId).orElseThrow(() -> new MusicNotFoundException("Music not found"))
                );
    }

    @Override
    public List<MusicDTO> findAll() throws ExecutionControl.NotImplementedException {
        return null;
    }

    public MusicEntity findMusicByIsrc(String isrc) {
        return musicRepository.findMusicIdByMusicIsrc(isrc).orElseThrow(() -> new MusicNotFoundException("No music is corresponding to the isrc %s".formatted(isrc)));
    }
}
