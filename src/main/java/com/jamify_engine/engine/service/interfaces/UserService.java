package com.jamify_engine.engine.service.interfaces;

import com.jamify_engine.engine.models.dto.UserDTO;
import com.jamify_engine.engine.service.ServiceBasics;
import com.jamify_engine.engine.models.entities.UserEntity;

public interface UserService extends ServiceBasics<UserDTO> {
    UserDTO findByEmail(String email);

    UserEntity findEntityByEmail(String email);

    UserDTO update(Long id, UserDTO user);

    UserDTO update(Long id, UserEntity user);
}
