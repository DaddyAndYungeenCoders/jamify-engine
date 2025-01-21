package com.jamify_engine.engine.repository;

import com.jamify_engine.engine.models.entities.TagEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TagsRepository extends JpaRepository<TagEntity, Long> {
    Optional<TagEntity> findByLabel(String label);
}
