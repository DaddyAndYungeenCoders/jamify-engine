package com.jamify_engine.engine.controllers;

import com.jamify_engine.engine.models.dto.event.EventCreateDTO;
import com.jamify_engine.engine.models.dto.event.EventDTO;
import com.jamify_engine.engine.service.interfaces.EventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import jdk.jshell.spi.ExecutionControl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/events")
public class EventController extends CRUDController<EventDTO, EventService> {

    @Autowired
    public EventController(EventService eventService) {
        this.service = eventService;
    }

    @Operation(summary = "Create a new hosted event",
            description = "Create a new hosted event in the Jamify Engine.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Hosted event created successfully."),
            })
    @PostMapping("/createHostedEvent")
    public EventDTO create(@Valid @RequestBody EventCreateDTO eventDTO) {
        log.info("[REST] POST /createHostedEvent - Creating new hosted event");
        return service.createHostedEvent(eventDTO);
    }

}
