package com.jamify_engine.engine.repository;

import com.jamify_engine.engine.models.entities.EventEntity;
import com.jamify_engine.engine.models.enums.EventStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.Set;

public interface EventRepository extends JpaRepository<EventEntity, Long> {

    @Query("SELECT e FROM EventEntity e WHERE e.host.id = :hostId")
    Set<EventEntity> findAllByHostId(long hostId);

    Optional<Set<EventEntity>> findAllByStatus(EventStatus status);
}
