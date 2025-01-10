package com.jamify_engine.engine.models.dto.external.spotify;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class SpotifyTrackDTO {
    private String name;
    private int popularity;
    @JsonProperty("preview_url")
    private String previewUrl;
    private String uri;
    @JsonProperty("is_local")
    private boolean isLocal;
    @JsonProperty("device")
    private SpotifyDeviceDTO device;
    @JsonProperty("context")
    private SpotifyContextDTO context;
    @JsonProperty("actions")
    private SpotifyActionsDTO actions;
    @JsonProperty("external_ids")
    private SpotifyExternalIdsDTO externalIds;
}
