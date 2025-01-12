package com.jamify_engine.engine.models.dto;

import lombok.Builder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
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
    public static class UserDTOBuilder {
        UserDTOBuilder() {
            name = "default name";
            email = "default email";
            country = "FR";
            provider = "Spotify";
            imgUrl = "default image";
            userProviderId = "default userProviderId";
            roles = new HashSet<>();
            jams = new ArrayList<>();
            hasJamRunning = false;
        }
    }
}
