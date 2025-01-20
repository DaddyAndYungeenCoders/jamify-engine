package com.jamify_engine.engine.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class JwtService {

    @Autowired
    private JwtDecoder jwtDecoder;

    /**
     * Validates the given JWT token.
     *
     * @param jwt the JWT token to validate
     * @return true if the token is valid, false otherwise
     */
    public boolean validateToken(String jwt) {
        try {
            Jwt decodedJwt = jwtDecoder.decode(jwt);
            return true;
        } catch (Exception e) {
            log.error("Invalid JWT token: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Extracts the username (email) from the given JWT token.
     *
     * @param token the JWT token
     * @return the username (email) extracted from the token
     */
    public String getUsernameFromToken(String token) {
        log.info("Getting username from token: {}", token);
        Jwt decodedJwt = jwtDecoder.decode(token);
        return decodedJwt.getClaimAsString("email");
    }

    /**
     * Extracts the roles from the given JWT token.
     *
     * @param token the JWT token
     * @return the list of roles extracted from the token
     */
    public List<String> getRolesFromToken(String token) {
        log.info("Getting roles from token: {}", token);
        Jwt decodedJwt = jwtDecoder.decode(token);
        return decodedJwt.getClaimAsStringList("roles");
    }
}