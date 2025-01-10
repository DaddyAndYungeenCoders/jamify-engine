package com.jamify_engine.engine.models.dto.external.spotify;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class SpotifyExternalIdsDTO {
    /**
     * For Jamify, we prefer the IRSC
     */
    private String isrc;
    private String ean;
    private String upc;
}
