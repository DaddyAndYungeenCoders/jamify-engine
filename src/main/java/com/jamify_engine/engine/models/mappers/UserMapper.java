package com.jamify_engine.engine.models.mappers;

import com.jamify_engine.engine.models.dto.UserDTO;
import com.jamify_engine.engine.models.entities.UserEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserEntity toEntity(UserDTO userDTO);

    UserDTO toDTO(UserEntity userEntity);

    List<UserEntity> toEntities(List<UserDTO> userDTOs);

    List<UserDTO> toDTOs(List<UserEntity> userEntities);

}
