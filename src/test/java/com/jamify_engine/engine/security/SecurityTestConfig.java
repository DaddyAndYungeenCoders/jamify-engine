package com.jamify_engine.engine.security;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import java.util.Set;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@TestConfiguration
public class SecurityTestConfig {
    // TODO discuss
    @Bean
    public JwtService jwtService() {
        JwtService mockJwtService = mock(JwtService.class);
        when(mockJwtService.validateToken(anyString())).thenReturn(true);
        when(mockJwtService.getUsernameFromToken(anyString())).thenReturn("test@test.com");
        when(mockJwtService.getRolesFromToken(anyString())).thenReturn(Set.of("USER"));
        return mockJwtService;
    }
}
