package com.jamify_engine.engine.models.dto.external.spotify;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class SpotifyContextDTO {
    private String type;
    private String href;

    @JsonProperty("external_urls")
    private ExternalUrlsDTO externalUrls;

    private String uri;

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    @Builder
    public static class ExternalUrlsDTO {
        private String spotify;
    }
}
