package com.jamify_engine.engine.models.dto;

import lombok.Builder;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

@Builder
public record UserDTO(
        String name,
        String email,
        String country,
        String provider,
        String imgUrl,
        String userProviderId,
        Set<String> roles,
        List<JamDTO> jams,
        boolean hasJamRunning

        // TODO: Add more fields
) implements Serializable {
}
