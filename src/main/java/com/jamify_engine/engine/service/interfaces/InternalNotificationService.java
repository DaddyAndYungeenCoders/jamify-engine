package com.jamify_engine.engine.service.interfaces;

import com.jamify_engine.engine.models.vms.NotificationVM;

public interface InternalNotificationService {
    void sendNotification(NotificationVM notificationVM);
}
