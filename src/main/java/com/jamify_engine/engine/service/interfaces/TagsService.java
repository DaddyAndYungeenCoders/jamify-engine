package com.jamify_engine.engine.service.interfaces;

import com.jamify_engine.engine.models.entities.TagEntity;

import java.util.Optional;

public interface TagsService {
    Optional<TagEntity> findByLabel(String label);

    TagEntity createNewTag(TagEntity tags);
}
