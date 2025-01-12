package com.jamify_engine.engine.models.dto;

import com.jamify_engine.engine.models.entities.TagEntity;
import lombok.*;

import java.io.Serializable;
import java.util.Set;

@Builder
public record MusicDTO(
        Long id,
        String isrc,
        String author,
        String title,
        String imgUrl,
        String tempo,
        String energy,
        Set<TagDTO> tags
) {
}
