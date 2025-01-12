package com.jamify_engine.engine.repository;

import com.jamify_engine.engine.models.entities.MusicEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface MusicRepository extends JpaRepository<MusicEntity, Long> {
    @Query("SELECT m FROM music m WHERE m.isrc = ?1")
    Optional<MusicEntity> findMusicEntityByIsrc(String isrc);
}
