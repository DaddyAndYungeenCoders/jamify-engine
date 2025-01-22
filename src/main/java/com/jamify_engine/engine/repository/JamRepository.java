package com.jamify_engine.engine.repository;

import com.jamify_engine.engine.models.entities.JamEntity;
import com.jamify_engine.engine.models.enums.JamStatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface JamRepository extends JpaRepository<JamEntity, Long> {
    @Query("SELECT j FROM JamEntity j " +
            "JOIN j.participants p " +
            "WHERE j.status = :status " +
            "AND p.user.id = :userId AND p.isHost = true " +
            "ORDER BY j.id")
    Optional<JamEntity> findFirstByStatusAndParticipantUserId(
            @Param("status") JamStatusEnum status,
            @Param("userId") Long userId);
    Optional<List<JamEntity>> findAllByStatus(JamStatusEnum status);
}
