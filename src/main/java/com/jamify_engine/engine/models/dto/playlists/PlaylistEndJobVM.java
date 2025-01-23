package com.jamify_engine.engine.models.dto.playlists;

import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PlaylistEndJobVM {
    UUID id;
    String userProviderId;
    PlaylistEndJobPayloadVM data;
}
