package com.jamify_engine.engine.models.dto.external.spotify;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class SpotifyActionsDTO {
    @JsonProperty("interrupting_playback")
    private boolean interruptingPlayback;

    private boolean pausing;

    private boolean resuming;

    private boolean seeking;

    @JsonProperty("skipping_next")
    private boolean skippingNext;

    @JsonProperty("skipping_prev")
    private boolean skippingPrev;

    @JsonProperty("toggling_repeat_context")
    private boolean togglingRepeatContext;

    @JsonProperty("toggling_shuffle")
    private boolean togglingShuffle;

    @JsonProperty("toggling_repeat_track")
    private boolean togglingRepeatTrack;

    @JsonProperty("transferring_playback")
    private boolean transferringPlayback;
}

