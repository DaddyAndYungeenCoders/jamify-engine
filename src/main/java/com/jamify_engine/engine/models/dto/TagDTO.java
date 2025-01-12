package com.jamify_engine.engine.models.dto;

import lombok.Builder;

@Builder
public record TagDTO (Long id, String label) {}
