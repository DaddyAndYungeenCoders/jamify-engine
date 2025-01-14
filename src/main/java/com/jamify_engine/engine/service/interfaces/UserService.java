package com.jamify_engine.engine.service.interfaces;

import com.jamify_engine.engine.models.dto.UserDTO;
import com.jamify_engine.engine.service.ServiceBasics;
import com.jamify_engine.engine.models.entities.UserEntity;

import java.util.Set;

public interface UserService extends ServiceBasics<UserDTO> {
    UserDTO findByEmail(String email);

    UserEntity findEntityByEmail(String email);

    UserDTO update(Long id, UserDTO user);

    UserDTO updateFromRegisteringOrLoggingIn(Long id, UserDTO dto);

    UserDTO update(Long id, UserEntity user);

    Set<UserEntity> findAllEntitiesByIds(Set<Long> ids);
}
