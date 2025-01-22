package com.jamify_engine.engine.repository;

import com.jamify_engine.engine.models.entities.JamParticipantEntity;
import com.jamify_engine.engine.models.entities.JamParticipantId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface JamParticipantRepository extends JpaRepository<JamParticipantEntity, JamParticipantId> {
    Set<JamParticipantEntity> findAllById_UserId(Long userId);
}
