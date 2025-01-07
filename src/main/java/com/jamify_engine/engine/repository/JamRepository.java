package com.jamify_engine.engine.repository;

import com.jamify_engine.engine.models.entities.JamEntity;
import com.jamify_engine.engine.models.enums.JamStatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JamRepository extends JpaRepository<JamEntity, Long> {
    Optional<JamEntity> findFirstByHost_IdAndStatus(Long hostId, JamStatusEnum jamStatusEnum);
}
