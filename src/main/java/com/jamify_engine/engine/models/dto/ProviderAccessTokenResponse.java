package com.jamify_engine.engine.models.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class ProviderAccessTokenResponse {
    @JsonProperty("access_token")
//    @JsonAlias("id_token") for other providers if needed...
    @Schema(name = "Value of the access token", example = "faz614f436a+az9ef1....")
    private String accessToken;
    @JsonProperty("token_type")
    @Schema(name = "Type of the token", example = "Bearer")
    private String tokenType;
    @JsonProperty("expires_in")
    @Schema(name = "Time in seconds until the token expires", example = "3600")
    private String expiresIn;
    @JsonProperty("refresh_token")
    @Schema(name = "Refresh token", example = "faz614f436a+az9ef1....")
    private String refreshToken;
    @Schema(name = "Scope of the token", example = "user-read-private user-read-email")
    private String scope;

}
