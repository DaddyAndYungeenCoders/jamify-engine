package com.jamify_engine.engine.models.dto.playlists;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SpotifyPlaylistDTO implements PlaylistDTO {
    // The spotify ID of the playlist, NB: there is maybe no need to divide the types, but it is a good practice to do so
    private String id;
    private String name;
    private String description;
    @JsonProperty(value = "snapshot_id")
    private String snapshotId; // This is the spotify answer when we add a new music to a playlist
}
