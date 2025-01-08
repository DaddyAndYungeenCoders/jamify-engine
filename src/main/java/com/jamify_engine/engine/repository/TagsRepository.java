package com.jamify_engine.engine.repository;

import com.jamify_engine.engine.models.entities.TagEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface TagsRepository extends JpaRepository<TagEntity, Long> {
    Optional<List<TagEntity>> findAllByLabelIn(Set<String> themes);
}
