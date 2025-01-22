package com.jamify_engine.engine.models.mappers;

import com.jamify_engine.engine.models.dto.JamDTO;
import com.jamify_engine.engine.models.dto.UserDTO;
import com.jamify_engine.engine.models.dto.event.EventParticipantDTO;
import com.jamify_engine.engine.models.entities.JamEntity;
import com.jamify_engine.engine.models.entities.JamParticipantEntity;
import com.jamify_engine.engine.models.entities.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UserMapper {
    JamMapper JAM_MAPPER = Mappers.getMapper(JamMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "accessToken", ignore = true)
    @Mapping(target = "playlists", ignore = true)
    @Mapping(target = "jams", ignore = true)
    UserEntity toEntity(UserDTO userDTO);

    @Mapping(target = "jams", source = "jams", qualifiedByName = "mapJams")
    UserDTO toDTO(UserEntity userEntity);

    List<UserEntity> toEntities(List<UserDTO> userDTOs);

    List<UserDTO> toDTOs(List<UserEntity> userEntities);

    void updateEntityFromDto(UserDTO userDTO, @MappingTarget UserEntity userEntity);

    @Named("mapJams")
    default List<JamDTO> mapJamParticipationToLongs(Set<JamParticipantEntity> jamParticipantEntitySet) {
        List<JamDTO> result = new ArrayList<>();

        if (jamParticipantEntitySet == null) {
            return new ArrayList<>();
        }

        jamParticipantEntitySet.forEach(participation ->
                result.add(JAM_MAPPER.toDTO(participation.getJam()))
        );

        return result;
    }
}
