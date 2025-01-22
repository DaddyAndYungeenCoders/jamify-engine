package com.jamify_engine.engine.service.interfaces;

import com.jamify_engine.engine.models.dto.UserDTO;
import com.jamify_engine.engine.models.entities.JamParticipantEntity;
import com.jamify_engine.engine.models.entities.UserEntity;

import java.util.Set;

public interface ParticipantService {
    JamParticipantEntity createJamParticipant(JamParticipantEntity jamParticipantEntity);
    JamParticipantEntity updateJamParticipant(JamParticipantEntity jamParticipantEntity);
    JamParticipantEntity deleteJamParticipant(JamParticipantEntity jamParticipantEntity);
    Set<JamParticipantEntity> getFromUserId(Long userId);
}
