package com.jamify_engine.engine.models.mappers;

import com.jamify_engine.engine.models.dto.playlists.PlaylistDTO;
import com.jamify_engine.engine.models.entities.PlaylistEntity;

public interface PlaylistMapper<D extends PlaylistDTO, E extends PlaylistEntity> {
    D toDTO(E entity);
    E toEntity(D dto);
}
