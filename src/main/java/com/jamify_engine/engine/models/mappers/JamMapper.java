package com.jamify_engine.engine.models.mappers;

import com.jamify_engine.engine.exceptions.user.UserNotFoundException;
import com.jamify_engine.engine.models.dto.JamDTO;
import com.jamify_engine.engine.models.entities.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface JamMapper {
    @Mapping(source = "id", target = "jamId")
    @Mapping(target = "themes", source = "tags", qualifiedByName = "mapTagsToThemes")
    @Mapping(target = "participants", source = "participants", qualifiedByName = "mapParticipants")
    @Mapping(target = "messages", source = "messages", qualifiedByName = "mapMessages")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "userProviderId", source = "participants", qualifiedByName = "setUserProviderId")
    JamDTO toDTO(JamEntity entity);

    @Mapping(source = "id", target = "jamId")
    @Mapping(target = "themes", source = "tags", qualifiedByName = "mapTagsToThemes")
    @Mapping(target = "participants", source = "participants", qualifiedByName = "mapParticipants")
    @Mapping(target = "messages", source = "messages", qualifiedByName = "mapMessages")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "userProviderId", source = "participants", qualifiedByName = "setUserProviderId")
    List<JamDTO> toDTO(List<JamEntity> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tags", expression = "java(mapThemesToTags(dto.themes()))")
    @Mapping(target = "participants", ignore = true)
    @Mapping(target = "messages", expression = "java(mapMessageIds(dto.messages()))")
    @Mapping(source = "scheduledDate", target = "schedStart")
    JamEntity toEntity(JamDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tags", expression = "java(mapThemesToTags(dto.themes()))")
    @Mapping(target = "participants", ignore = true)
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
        if (messageEntities == null) {
            return new HashSet<>();
        }

        return messageEntities.stream()
                .map(JamMessageEntity::getId)
                .collect(Collectors.toSet());
    }

    @Named("mapHost")
    default Long mapHost(UserEntity host) {
        return host.getId();
    }

    @Named("mapParticipants")
    default Set<Long> mapParticipantsToIds(Set<JamParticipantEntity> participants) {
        if (participants == null) {
            return new HashSet<>();
        }
        return participants.stream()
                .map(participant -> participant.getUser().getId())
                .collect(Collectors.toSet());
    }

    @Named("setUserProviderId")
    default String setHostProviderIdFromParticipants(Set<JamParticipantEntity> jamParticipantEntitySet) {
        return jamParticipantEntitySet.stream()
                .filter(JamParticipantEntity::isHost)
                .findFirst()
                .map(jamParticipant -> jamParticipant.getUser().getUserProviderId())
                .orElseThrow(() -> new UserNotFoundException("The jam has no host :("));
    }
}
