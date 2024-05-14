package com.kobylchak.carsharing.service.notification;

import com.kobylchak.carsharing.service.notification.message.MessageBuilder;

public interface NotificationService {
    void sendNotification(String text);
    
    MessageBuilder messageBuilder();
}
