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
    @Schema(description = "Access token", example = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJEYW1pZW4gTWFpbGhlYmlhdSIsImlzcyI6Imh0dHA6Ly9sb2NhbGhvc3Q6ODA4MSIsImVtYWlsIjoiZGFtaWVubWFpbGhlYmlhdUBnbWFpbC5jb20iLCJyb2xlcyI6WyJST0xFX1VTRVIiXSwiY291bnRyeSI6IkZSIiwicHJvdmlkZXIiOiJzcG90aWZ5IiwiaWQiOjQsImlhdCI6MTczNTQ5NzAwOSwiZXhwIjoxNzY3MDUyODAwfQ.RLN_GL4S8Me7yEFLdGr6wTf-AweABF34gHL0fHjnaoxyaqitcvjjdJLeVl-K6Y5fVxZjX1RMlfY5i-hfbHTAn3g_Wy6zD8v05vVfdwgqQzT5ZrzQlDFg73ogHhRYRMfep0xFTYIOe1uhvMu2CWT2KHdc20VE32sapwzoVdkUXU8EKES3scYOkf1hA_rzQDi64afRjoN-1UEpOJNOiwJ0wk51GXvI7tLXkBeKBwz2DBXgTHDrblj5uyBx8lICxGzuKy8-5k9Mjk4R5EtpDHIH9ILhdTIb5O3u8iJnWrr3Lykxj4u748VA7I6ZsraUNXf2fNqlkrx0dppn05dqz-uobA")
    String accessToken,
    @Schema(description = "Expiration time of the access token", example = "2021-08-01T12:00:00")
    LocalDateTime expiresAt
) {}

