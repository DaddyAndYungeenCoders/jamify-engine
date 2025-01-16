package com.jamify_engine.engine.models.mappers;

import com.jamify_engine.engine.models.dto.JamDTO;
import com.jamify_engine.engine.models.entities.JamEntity;
import com.jamify_engine.engine.models.entities.JamMessageEntity;
import com.jamify_engine.engine.models.entities.TagEntity;
import com.jamify_engine.engine.models.entities.UserEntity;
import org.mapstruct.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface JamMapper {
    @Mapping(target = "themes", source = "tags", qualifiedByName = "mapTagsToThemes")
    @Mapping(target = "participants", source = "participants", qualifiedByName = "mapParticipants")
    @Mapping(target = "hostId", source = "host", qualifiedByName = "mapHost")
    @Mapping(target = "messages", source = "messages", qualifiedByName = "mapMessages")
    @Mapping(target = "status", source = "status")
    JamDTO toDTO(JamEntity entity);

    @Mapping(target = "themes", source = "tags", qualifiedByName = "mapTagsToThemes")
    @Mapping(target = "participants", source = "participants", qualifiedByName = "mapParticipants")
    @Mapping(target = "hostId", source = "host", qualifiedByName = "mapHost")
    @Mapping(target = "messages", source = "messages", qualifiedByName = "mapMessages")
    @Mapping(target = "status", source = "status")
    List<JamDTO> toDTO(List<JamEntity> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "host", ignore = true)
    @Mapping(target = "hostId", source = "hostId")
    @Mapping(target = "tags", expression = "java(mapThemesToTags(dto.themes()))")
    @Mapping(target = "participants", expression = "java(mapParticipantIds(dto.participants()))")
    @Mapping(target = "messages", expression = "java(mapMessageIds(dto.messages()))")
    @Mapping(source = "scheduledDate", target = "schedStart")
    JamEntity toEntity(JamDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "host", ignore = true)
    @Mapping(target = "hostId", source = "hostId")
    @Mapping(target = "tags", expression = "java(mapThemesToTags(dto.themes()))")
    @Mapping(target = "participants", expression = "java(mapParticipantIds(dto.participants()))")
    @Mapping(target = "messages", expression = "java(mapMessageIds(dto.messages()))")
    @Mapping(source = "scheduledDate", target = "schedStart")
    List<JamEntity> toEntity(List<JamDTO> dto);

    // TODO use tagsMapperWhenItIsCreated
    @Named("mapThemesToTags")
    default Set<TagEntity> mapThemesToTags(Set<String> themes) {
        if (themes == null) {
            return new HashSet<>();
        }
        return themes.stream()
                .map(theme -> {
                    TagEntity tag = new TagEntity();
                    tag.setLabel(theme);
                    return tag;
                })
                .collect(Collectors.toSet());
    }

    @Named("mapParticipantIds")
    default Set<UserEntity> mapParticipantIds(Set<Long> participantIds) {
        if (participantIds == null) {
            return new HashSet<>();
        }
        return participantIds.stream()
                .map(id -> {
                    UserEntity user = new UserEntity();
                    user.setId(id);
                    return user;
                })
                .collect(Collectors.toSet());
    }

    // TODO use jammessageMapper when it is created
    @Named("mapMessageIds")
    default Set<JamMessageEntity> mapMessageIds(Set<Long> messageIds) {
        if (messageIds == null) {
            return new HashSet<>();
        }
        return messageIds.stream()
                .map(id -> {
                    JamMessageEntity message = new JamMessageEntity();
                    message.setId(id);
                    return message;
                })
                .collect(Collectors.toSet());
    }

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
