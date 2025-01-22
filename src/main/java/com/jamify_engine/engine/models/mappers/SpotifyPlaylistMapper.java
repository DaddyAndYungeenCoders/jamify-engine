package com.jamify_engine.engine.models.mappers;

import com.jamify_engine.engine.models.dto.playlists.SpotifyPlaylistDTO;
import com.jamify_engine.engine.models.entities.PlaylistEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface SpotifyPlaylistMapper extends PlaylistMapper<SpotifyPlaylistDTO, PlaylistEntity> {
    @Override
    SpotifyPlaylistDTO toDTO(PlaylistEntity entity);
    @Override
    PlaylistEntity toEntity(SpotifyPlaylistDTO dto);
}
