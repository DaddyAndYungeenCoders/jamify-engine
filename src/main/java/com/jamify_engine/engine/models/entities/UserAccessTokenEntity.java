package com.jamify_engine.engine.models.entities;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity(name = "user_access_tokens")
@Data
@Schema(description = "User access token details")
public class UserAccessTokenEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Schema(description = "User details")
    @OneToOne(fetch = FetchType.LAZY)
    private UserEntity user;

    @Schema(description = "Provider name", example = "spotify")
    private String provider;

    @Schema(description = "Access token", maxLength = 512, example = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.TJVA95OrM7E2cBab30RMHrHDcEfxjoYZgeFONFh7HgjueVfjZVZL4OzygP0c5b2f")
    @Column(length = 512)
    private String accessToken;

    @Schema(description = "Expiration time of the access token", example = "2021-08-01T12:00:00")
    private LocalDateTime expiresAt;
}
