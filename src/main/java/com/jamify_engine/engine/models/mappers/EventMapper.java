package com.jamify_engine.engine.models.mappers;

import com.jamify_engine.engine.models.dto.event.EventCreateDTO;
import com.jamify_engine.engine.models.dto.event.EventDTO;
import com.jamify_engine.engine.models.dto.event.EventParticipantDTO;
import com.jamify_engine.engine.models.entities.EventEntity;
import com.jamify_engine.engine.models.entities.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface EventMapper {

    @Mapping(target = "participants", source = "participants", qualifiedByName = "mapParticipantsToDto")
    EventDTO toDTO(EventEntity entity);

    @Mapping(target = "participants", source = "participants")
    EventEntity toEntity(EventDTO dto);

    List<EventDTO> toDTOList(List<EventEntity> entityList);

    List<EventEntity> toEntityList(List<EventDTO> dtoList);

    @Named("mapParticipantsToDto")
    default Set<EventParticipantDTO> mapParticipants(Set<UserEntity> participants) {
        if (participants == null || participants.isEmpty()) {
            return new HashSet<>();
        }
        return participants.stream()
                .map(this::toEventParticipantDto)
                .collect(Collectors.toSet());
    }

    default EventParticipantDTO toEventParticipantDto(UserEntity user) {
        EventParticipantDTO participant = new EventParticipantDTO();
        participant.setUsername(user.getName());
        participant.setEmail(user.getEmail());
        participant.setProfilePicture(user.getImgUrl());
        return participant;
    }

    default void updateEntityFromDTO(EventDTO dto, @MappingTarget EventEntity event) {
        if (dto == null) {
            return;
        }
        event.setName(dto.getName());
        event.setScheduledStart(dto.getScheduledStart());
        event.setStatus(dto.getStatus());
        event.setAddress(dto.getAddress());
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "participants", ignore = true)
    EventEntity toEntityfromCreateDTO(EventCreateDTO createDTO);
}

