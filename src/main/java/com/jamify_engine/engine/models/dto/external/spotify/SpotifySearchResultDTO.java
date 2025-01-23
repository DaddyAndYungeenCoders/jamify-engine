package com.jamify_engine.engine.models.dto.external.spotify;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SpotifySearchResultDTO {
    SpotifySearchFullRequestResponseWithItems tracks;
}
