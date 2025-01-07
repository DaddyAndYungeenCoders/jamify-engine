package com.jamify_engine.engine.controllers.auth;

import com.jamify_engine.engine.exceptions.security.InvalidApiKeyException;
import com.jamify_engine.engine.models.dto.UserAccessTokenDto;
import com.jamify_engine.engine.security.TokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Value("${config.uaa.jamify-engine-api-key}")
    private String jamifyEngineApiKey;

    private final TokenService tokenService;

    public AuthController(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Operation(summary = "Save access token",
            description = "Endpoint to save an access token. Only authorized to Jamify Engine.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Access token refreshed successfully."),
                    @ApiResponse(responseCode = "401", description = "Unauthorized.")
            })
    @PostMapping("/access-token")
    public ResponseEntity<Void> saveAccessToken(@RequestHeader(value = "X-API-KEY") String apiKey,
                                                @RequestBody UserAccessTokenDto userAccessToken) {
        log.info("Received request to save access token : {} for user: {}", userAccessToken.accessToken(), userAccessToken.email());
        if (!apiKey.equals(jamifyEngineApiKey)) {
            throw new InvalidApiKeyException("Invalid API key");
        }

        tokenService.saveAccessToken(userAccessToken);
        return ResponseEntity.ok().build();
    }
}
