package com.jamify_engine.engine.models.dto.event;

import com.jamify_engine.engine.models.entities.AddressType;
import io.swagger.v3.oas.annotations.media.Schema;
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
    @Schema(description = "Event name", example = "Grosse rapta")
    private String name;

    @NotNull(message = "Start date is required")
    @Future(message = "Event must be in the future")
    @Schema(description = "Event start date", example = "2021-08-01T12:00:00")
    private LocalDateTime scheduledStart;

    @Valid
    @NotNull(message = "Address is required")
    @Schema(description = "Event address", $schema = "AddressType")
    private AddressType address;
}
