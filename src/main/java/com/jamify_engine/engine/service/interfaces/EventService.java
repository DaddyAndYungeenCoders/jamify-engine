package com.jamify_engine.engine.service.interfaces;

import com.jamify_engine.engine.models.dto.event.EventCreateDTO;
import com.jamify_engine.engine.models.dto.event.EventDTO;
import com.jamify_engine.engine.models.entities.EventStatus;
import com.jamify_engine.engine.service.ServiceBasics;

import java.util.List;

public interface EventService extends ServiceBasics<EventDTO> {
    EventDTO createHostedEvent(EventCreateDTO eventDTO);

    List<EventDTO> findAllByHostId(long hostId);

    EventDTO joinEvent(Long eventId);

    void cancelEvent(Long eventId);

    void leaveEvent(Long eventId);

    List<EventDTO> findByStatus(EventStatus status);
}
