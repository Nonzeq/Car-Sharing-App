package com.kobylchak.carsharing.service.notification.message.impl;

import com.kobylchak.carsharing.service.notification.message.NotificationMessage;
import org.springframework.stereotype.Component;

@Component
public class TelegramNotificationMessage
        implements NotificationMessage<TelegramMessageBuilder> {
    @Override
    public TelegramMessageBuilder builder() {
        return new TelegramMessageBuilder();
    }
}
