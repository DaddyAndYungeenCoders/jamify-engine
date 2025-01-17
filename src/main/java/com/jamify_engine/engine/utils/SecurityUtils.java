package com.jamify_engine.engine.utils;

import com.jamify_engine.engine.models.entities.UserEntity;
import com.jamify_engine.engine.service.interfaces.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtils {

    private static UserService userService;

    @Autowired
    public SecurityUtils(UserService userService) {
        SecurityUtils.userService = userService;
    }

    public static UserEntity getCurrentUser() {
        Object email = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userService.findEntityByEmail(email.toString());
    }

    public static UserEntity findByEmail(String email) {
        return userService.findEntityByEmail(email);
    }
}
