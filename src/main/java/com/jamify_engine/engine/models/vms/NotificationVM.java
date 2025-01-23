package com.jamify_engine.engine.models.vms;

import com.jamify_engine.engine.models.dto.notifications.CustomMetadata;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NotificationVM {
    private String destId;

    private String roomId;

    @NotBlank
    private String title;

    @NotBlank
    private String content;

    private CustomMetadata metadata;
}
