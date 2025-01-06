package com.jamify_engine.engine.service.interfaces;

import com.jamify_engine.engine.models.dto.UserDTO;
import com.jamify_engine.engine.service.ServiceBasics;

public interface UserService extends ServiceBasics<UserDTO> {
    UserDTO findByEmail(String email);
}
