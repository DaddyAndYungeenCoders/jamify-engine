package com.jamify_engine.engine.models.enums;

import lombok.Getter;

@Getter
public enum EventStatus {
    SCHEDULED("SCHEDULED"),
    CANCELLED("CANCELLED"),
    STARTED("STARTED"),
    FINISHED("FINISHED");

    private final String status;

    EventStatus(String status) {
        this.status = status;
    }
}
