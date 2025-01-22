package com.jamify_engine.engine.service.implementations;

import com.jamify_engine.engine.models.entities.JamParticipantEntity;
import com.jamify_engine.engine.repository.JamParticipantRepository;
import com.jamify_engine.engine.service.interfaces.ParticipantService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class ParticipantServiceImpl implements ParticipantService {
    private final JamParticipantRepository jamParticipantRepository;

    public ParticipantServiceImpl(JamParticipantRepository jamParticipantRepository) {
        this.jamParticipantRepository = jamParticipantRepository;
    }

    @Override
    public JamParticipantEntity createJamParticipant(JamParticipantEntity jamParticipantEntity) {
        return jamParticipantRepository.save(jamParticipantEntity);
    }

    @Override
    public JamParticipantEntity updateJamParticipant(JamParticipantEntity jamParticipantEntity) {
        return jamParticipantRepository.save(jamParticipantEntity);
    }

    @Override
    public JamParticipantEntity deleteJamParticipant(JamParticipantEntity jamParticipantEntity) {
        jamParticipantRepository.delete(jamParticipantEntity);
        return jamParticipantEntity;
    }

    @Override
    public Set<JamParticipantEntity> getFromUserId(Long userId) {
        return jamParticipantRepository.findAllById_UserId(userId);
    }
}
