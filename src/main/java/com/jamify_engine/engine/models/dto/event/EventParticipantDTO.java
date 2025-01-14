package com.jamify_engine.engine.models.dto.event;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class EventParticipantDTO {
    @Schema(description = "User full name", example = "John Doe")
    private String username;
    @Schema(description = "User email", example = "user@example.com")
    private String email;
    @Schema(description = "profile picture", example = "https://example.com/profile.jpg")
    private String profilePicture;
}
