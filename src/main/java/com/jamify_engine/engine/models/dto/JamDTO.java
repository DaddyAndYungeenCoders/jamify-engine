package com.jamify_engine.engine.models.dto;

import com.jamify_engine.engine.models.enums.JamStatusEnum;
import lombok.*;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

@Builder
public record JamDTO(String name,
                     Long hostId,
                     JamStatusEnum status,
                     Set<String> themes,
                     Set<Long> participants,
                     Set<Long> messages) implements Serializable { }
