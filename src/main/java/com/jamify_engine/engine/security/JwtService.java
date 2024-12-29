package com.jamify_engine.engine.security;

import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.List;

@Slf4j
@Service
public class JwtService {

    @Value("${security.jwt.private-key}")
    private String privateKeyPath;

    private RSAPrivateKey key;

    public JwtService() {
        try {
            key = loadPrivateKey(new File(privateKeyPath));
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
                key = loadPrivateKey(new File(privateKeyPath));
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
     * @return the list of roles extracted from the token
     */
    public List<String> getRolesFromToken(String token) {
        log.info("Getting roles from token: {}", token);
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("roles", List.class);
    }

    private RSAPrivateKey loadPrivateKey(File file) throws Exception {
        log.debug("[DEV] - Loading private key with path {}", file.toPath());
        String key = Files.readString(file.toPath());
        String privateKeyPEM = key
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----BEGIN RSA PRIVATE KEY-----", "")  // Support both formats
                .replace("-----END PRIVATE KEY-----", "")
                .replace("-----END RSA PRIVATE KEY-----", "")
                .replaceAll("\\s", "");

        byte[] encoded = Base64.getDecoder().decode(privateKeyPEM);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
        return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
    }
}
