package com.jamify_engine.engine.models.dto.playlists;

import lombok.*;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PlaylistEndJobPayloadVM {
    Set<Long> musics;
}