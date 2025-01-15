package com.jamify_engine.engine.service.interfaces;

import com.jamify_engine.engine.models.dto.event.EventCreateDTO;
import com.jamify_engine.engine.models.dto.event.EventDTO;
import com.jamify_engine.engine.models.entities.EventEntity;
import com.jamify_engine.engine.service.ServiceBasics;

import java.util.Set;

public interface EventService extends ServiceBasics<EventDTO> {
    EventDTO createHostedEvent(EventCreateDTO eventDTO);

    Set<EventEntity> findAllByHostId(long hostId);

    EventDTO joinEvent(Long eventId);

    void cancelEvent(Long eventId);

    void leaveEvent(Long eventId);
}
