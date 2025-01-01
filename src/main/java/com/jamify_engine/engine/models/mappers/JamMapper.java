package com.jamify_engine.engine.models.mappers;

import com.jamify_engine.engine.models.dto.JamDTO;
import com.jamify_engine.engine.models.entities.JamEntity;
import com.jamify_engine.engine.models.entities.JamMessageEntity;
import com.jamify_engine.engine.models.entities.TagEntity;
import com.jamify_engine.engine.models.entities.UserEntity;
import org.mapstruct.*;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface JamMapper {
    @Mapping(target = "themes", source = "tags", qualifiedByName = "mapTagsToThemes") // ici, themes est un Set<String> et tags est un Set<TagEntity>, j'aimerais utiliser le TagEntity.label en guise de themes
    @Mapping(target = "participants", source = "participants", qualifiedByName = "mapParticipants")
    @Mapping(target = "hostId", source = "host", qualifiedByName = "mapHost")
    @Mapping(target = "messages", source = "messages", qualifiedByName = "mapMessages")
    @Mapping(target = "status", source = "status")
    JamDTO toDTO(JamEntity entity);

    @Named("mapTagsToThemes")
    default Set<String> mapTagsToThemes(Set<TagEntity> tags) {
        return tags.stream()
                .map(TagEntity::getLabel)
                .collect(Collectors.toSet());
    }

    @Named("mapMessages")
    default Set<Long> mapMessages(Set<JamMessageEntity> messageEntities) {
        return messageEntities.stream()
                .map(JamMessageEntity::getId)
                .collect(Collectors.toSet());
    }

    @Named("mapHost")
    default Long mapHost(UserEntity host) {
        return host.getId();
    }

    @Named("mapParticipants")
    default Set<Long> mapParticipants(Set<UserEntity> participants) {
        return participants.stream()
                .map(UserEntity::getId)
                .collect(Collectors.toSet());
    }
}
