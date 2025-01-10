package com.jamify_engine.engine.models.dto.event;

import lombok.Data;

@Data
public class EventParticipantDTO {
    private String username;
    private String email;
    private String profilePicture;
}
