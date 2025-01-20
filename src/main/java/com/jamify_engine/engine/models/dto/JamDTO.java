package com.jamify_engine.engine.models.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jamify_engine.engine.models.enums.JamStatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Builder
public record JamDTO(
        @Schema(description = "Jam Id", example = "1")
        @JsonProperty("id")
        Long JamId,
        @Schema(description = "Jam name", example = "Jam salut salut")
        String name,
        @Schema(description = "Jam description", example = "A jam to celebrate the release of the new album")
        Long hostId,
        @Schema(description = "Jam status", example = "RUNNING", $schema = "JamStatusEnum")
        JamStatusEnum status,
        @Schema(description = "Jam themes", example = "['rap', 'hip-hop']")
        Set<String> themes,
        @Schema(description = "Jam participants", example = "[1, 2, 3]")
        Set<Long> participants,
        @Schema(description = "Jam messages. A voir si on laisse comme ca avec le save côté chat microservice", example = "[1, 2, 3]")
        Set<Long> messages,
        @Schema(description = "Jam scheduled date", example = "2021-08-01T12:00:00")
        LocalDateTime scheduledDate
) implements Serializable {}
