package com.jamify_engine.engine.models.dto.event;

import com.jamify_engine.engine.models.entities.AddressType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EventCreateDTO {
    @NotBlank(message = "Event name is required")
    @Size(min = 3, max = 100, message = "Name must be between 3 and 100 characters")
    private String name;

    @NotNull(message = "Start date is required")
    @Future(message = "Event must be in the future")
    private LocalDateTime scheduledStart;

    @Valid
    @NotNull(message = "Address is required")
    private AddressType address;
}
