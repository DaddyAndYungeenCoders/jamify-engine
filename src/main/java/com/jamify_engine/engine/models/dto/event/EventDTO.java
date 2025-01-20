package com.jamify_engine.engine.models.dto.event;

import com.jamify_engine.engine.models.entities.AddressType;
import com.jamify_engine.engine.models.enums.EventStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;

@Data
public class EventDTO implements Serializable {
    @Schema(description = "Event ID", example = "1")
    private Long id;
    @Schema(description = "Event name", example = "Grosse rapta party")
    private String name;
    @Schema(description = "Event description", example = "A party to celebrate the release of the new album")
    private LocalDateTime scheduledStart;
    @Schema(description = "Event status", example = "CREATED", $schema = "EventStatus")
    private EventStatus status;
    @Schema(description = "Event address", example = "1234 Main St, Springfield, IL 62701")
    private AddressType address;
    @Schema(description = "Event participants", $schema = "EventParticipantDTO")
    private Set<EventParticipantDTO> participants;
}
