package com.jamify_engine.engine.models.dto;

import lombok.*;

import java.io.Serializable;

@Builder
public record MusicDTO(Long id) implements Serializable {
}
