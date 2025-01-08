package com.jamify_engine.engine.security;

import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class JwtService {

    @Value("${security.jwt.public-key}")
    private String publicKeyPath;

    private RSAPublicKey key;

    public JwtService() {
        try {
            key = loadPublicKey(new File(publicKeyPath));
        } catch (Exception e) {
            log.error("Error loading private key: {}", e.getMessage());
        }
    }

    /**
     * Validates the given JWT token.
     *
     * @param jwt the JWT token to validate
     * @return true if the token is valid, false otherwise
     */
    public boolean validateToken(String jwt) {
        log.info("Validating token: {}", jwt);
        try {
            if (key == null) {
                key = loadPublicKey(new File(publicKeyPath));
            }

            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(jwt);
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
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("email", String.class);
    }

    /**
     * Extracts the roles from the given JWT token.
     *
     * @param token the JWT token
     * @return the set of roles extracted from the token
     */
    public Set<String> getRolesFromToken(String token) {
        log.debug("Getting roles from token: {}", token);
        List<?> rolesList = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("roles", List.class);
        Set<?> roles = new HashSet<>(rolesList);

        if (roles != null) {
            return roles.stream().toList().stream().map(role -> (String) role).collect(Collectors.toSet());
        }
        return Collections.emptySet();
    }


    private RSAPublicKey loadPublicKey(File file) throws Exception {
        log.debug("[DEV] - Loading private key with path {}", file.toPath());
        String key = Files.readString(file.toPath());
        String publicKeyPem = key
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----BEGIN RSA PUBLIC KEY-----", "")  // Support both formats
                .replace("-----END PUBLIC KEY-----", "")
                .replace("-----END RSA PUBLIC KEY-----", "")
                .replaceAll("\\s", "");

        byte[] encoded = Base64.getDecoder().decode(publicKeyPem);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(encoded);
        return (RSAPublicKey) keyFactory.generatePublic(keySpec);
    }
}
