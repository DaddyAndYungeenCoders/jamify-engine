package com.jamify_engine.engine.models.dto.notifications;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CustomMetadata {
    private String objectId;

    @Override
    public String toString() {
        return "CustomMetadata{" +
                "objectId='" + objectId + '\'' +
                '}';
    }
}

