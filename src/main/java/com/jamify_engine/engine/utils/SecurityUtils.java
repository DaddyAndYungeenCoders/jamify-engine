package com.jamify_engine.engine.utils;

import com.jamify_engine.engine.models.entities.UserEntity;
import com.jamify_engine.engine.security.authentication.JwtAuthentication;
import com.jamify_engine.engine.service.interfaces.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
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

    public static String getCurrentUserJwt() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication instanceof JwtAuthentication) {
            return ((JwtAuthentication) authentication).getJwtToken();
        }
        return null;
    }
}
