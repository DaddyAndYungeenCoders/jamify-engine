package com.jamify_engine.engine.models.dto.event;

import com.jamify_engine.engine.models.entities.AddressType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EventCreateDTO {
    private String name;
    private LocalDateTime scheduledStart;
    private AddressType address;
}
