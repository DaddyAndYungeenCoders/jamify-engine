package com.jamify_engine.engine.service.interfaces;

import com.jamify_engine.engine.models.dto.event.EventCreateDTO;
import com.jamify_engine.engine.models.dto.event.EventDTO;
import com.jamify_engine.engine.service.ServiceBasics;

public interface EventService extends ServiceBasics<EventDTO> {
    EventDTO createHostedEvent(EventCreateDTO eventDTO);
}
