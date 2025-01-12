package com.jamify_engine.engine.models.mappers;

import com.jamify_engine.engine.models.dto.TagDTO;
import com.jamify_engine.engine.models.entities.TagEntity;
import org.mapstruct.Mapper;

@Mapper
public interface TagMapper {
    TagDTO toDto(TagEntity entity);
    TagEntity toEntity(TagDTO dto);
}
