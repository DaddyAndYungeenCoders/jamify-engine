package com.jamify_engine.engine.repository;

import com.jamify_engine.engine.models.entities.EventEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<EventEntity, Long> {
}
