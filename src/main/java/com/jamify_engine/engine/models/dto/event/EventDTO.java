package com.jamify_engine.engine.models.dto.event;

import com.jamify_engine.engine.models.entities.AddressType;
import com.jamify_engine.engine.models.entities.EventStatus;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;

@Data
public class EventDTO implements Serializable {
    private Long id;
    private String name;
    private LocalDateTime scheduledStart;
    private EventStatus status;
    private AddressType address;
    private Set<EventParticipantDTO> participants;
}
