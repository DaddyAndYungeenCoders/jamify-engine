package com.jamify_engine.engine.models.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
@Schema(description = "User access token details. Will only be transferred to Jamify Engine.")
public record UserAccessTokenDto (
    @Schema(description = "User email", example = "user@example.com")
    String email,
    @Schema(description = "Provider name", example = "spotify")
    String provider,
    @Schema(description = "Access token", example = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.TJVA95OrM7E2cBab30RMHrHDcEfxjoYZgeFONFh7HgjueVfjZVZL4OzygP0c5b2f")
    String accessToken,
    @Schema(description = "Expiration time of the access token", example = "2021-08-01T12:00:00")
    LocalDateTime expiresAt
) {}

