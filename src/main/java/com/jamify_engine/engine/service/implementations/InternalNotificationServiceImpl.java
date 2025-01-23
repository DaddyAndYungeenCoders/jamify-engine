package com.jamify_engine.engine.service.implementations;

import com.jamify_engine.engine.models.vms.NotificationVM;
import com.jamify_engine.engine.service.interfaces.InternalNotificationService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;

@Service
public class InternalNotificationServiceImpl implements InternalNotificationService {

    private final WebClient orchestratorWebClient;

    public InternalNotificationServiceImpl(@Qualifier("orchestrationWebClient") WebClient webClient) {
        this.orchestratorWebClient = webClient;
    }

    @Override
    public void sendNotification(NotificationVM notificationVM) {
        HashMap<String, Object> bodyValue = new HashMap<>();
        HashMap<String, Object> metadata = new HashMap<>();
        metadata.put("objectId", notificationVM.getMetadata().getObjectId());

        bodyValue.put("destId", notificationVM.getDestId());
        bodyValue.put("roomId", notificationVM.getRoomId());
        bodyValue.put("title", notificationVM.getTitle());
        bodyValue.put("content", notificationVM.getContent());
        bodyValue.put("metadata", metadata);

        orchestratorWebClient.post()
                .uri("/api/v1/notifications")
                .bodyValue(bodyValue)
                .retrieve()
                .toBodilessEntity()
                .block();
    }
}
