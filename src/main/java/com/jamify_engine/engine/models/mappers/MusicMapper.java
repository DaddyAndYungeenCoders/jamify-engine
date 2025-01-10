package com.jamify_engine.engine.models.mappers;

import com.jamify_engine.engine.models.dto.MusicDTO;
import com.jamify_engine.engine.models.dto.TagDTO;
import com.jamify_engine.engine.models.entities.MusicEntity;
import com.jamify_engine.engine.models.entities.TagEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.HashSet;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface MusicMapper {
    TagMapper TAG_MAPPER = Mappers.getMapper(TagMapper.class);

    @Mapping(target = "tags", expression = "java(mapTagsEntityToTagsDTO(entity.getTags()))")
    MusicDTO toDTO(MusicEntity entity);

    @Mapping(target = "tags", expression = "java(mapTagsDTOToTagsEntities(dto.tags()))")
    MusicEntity toEntity(MusicDTO dto);

    @Named("mapTagsEntityToTagsDTO")
    default Set<TagDTO> mapTagsEntityToTagsDTO(Set<TagEntity> tagEntities) {
        Set<TagDTO> result = new HashSet<>();
        for (TagEntity tag: tagEntities) {
            result.add(TAG_MAPPER.toDto(tag));
        }
        return result;
    }

    @Named("mapTagsEntityToTagsDTO")
    default Set<TagEntity> mapTagsDTOToTagsEntities(Set<TagDTO> tagDTOS) {
        Set<TagEntity> result = new HashSet<>();
        for (TagDTO tag: tagDTOS) {
            result.add(TAG_MAPPER.toEntity(tag));
        }
        return result;
    }
}
