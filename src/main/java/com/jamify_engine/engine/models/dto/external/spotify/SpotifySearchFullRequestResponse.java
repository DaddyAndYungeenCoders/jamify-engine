package com.jamify_engine.engine.models.dto.external.spotify;

import lombok.*;

import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SpotifySearchFullRequestResponse {
    List<SpotifyTrackDTO> items;
}
