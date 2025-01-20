package com.jamify_engine.engine.security.authentication;

import lombok.Getter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@Getter
public class JwtAuthentication extends UsernamePasswordAuthenticationToken {
    private final String jwtToken;

    public JwtAuthentication(Object principal, String jwtToken, Collection<? extends GrantedAuthority> authorities) {
        super(principal, null, authorities);
        this.jwtToken = jwtToken;
    }
}
