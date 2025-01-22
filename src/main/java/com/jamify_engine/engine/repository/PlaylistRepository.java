package com.jamify_engine.engine.repository;

import com.jamify_engine.engine.models.entities.PlaylistEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaylistRepository extends JpaRepository<PlaylistEntity, Long> {
}
