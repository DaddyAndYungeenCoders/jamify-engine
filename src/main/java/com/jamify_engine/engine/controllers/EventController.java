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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
                    @ApiResponse(responseCode = "400", description = "Invalid event data."),
            })
    @PostMapping("/createHostedEvent")
    public EventDTO create(@Valid @RequestBody EventCreateDTO eventDTO) {
        log.info("[REST] POST /createHostedEvent - Creating new hosted event");
        return service.createHostedEvent(eventDTO);
    }

    @Operation(summary = "Join an event",
            description = "Join an SCHEDULED or RUNNING event in the Jamify Engine.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Event joined successfully."),
                    @ApiResponse(responseCode = "400", description = "Event is not joinable."),
            })
    @PostMapping("/join/{eventId}")
    public EventDTO joinEvent(@PathVariable Long eventId) {
        log.info("[REST] POST /join/{eventId} - Joining event with id: {}", eventId);
        return service.joinEvent(eventId);
    }

    @Operation(summary = "Cancel an event",
            description = "Cancel an SCHEDULED event in the Jamify Engine.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Event canceled successfully."),
                    @ApiResponse(responseCode = "400", description = "Event is not cancelable."),
                    @ApiResponse(responseCode = "404", description = "Event not found."),
            })
    @PutMapping("/cancel/{id}")
    public ResponseEntity cancelEvent(@PathVariable Long id) {
        log.info("[REST] PUT /cancel/{id} - Canceling event with id: {}", id);
        service.cancelEvent(id);
        return ResponseEntity.noContent().build();
    }
}
