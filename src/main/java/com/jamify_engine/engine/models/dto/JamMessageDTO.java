package com.jamify_engine.engine.models.dto;

import lombok.Builder;

import java.io.Serializable;
import java.time.LocalDateTime;

@Builder
public record JamMessageDTO(Long id, Long jamId, String content, Long author, LocalDateTime timestamp)
        implements Serializable {}
