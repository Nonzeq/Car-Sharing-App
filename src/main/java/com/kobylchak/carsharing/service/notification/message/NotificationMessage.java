package com.kobylchak.carsharing.service.notification.message;

public interface NotificationMessage<T extends MessageBuilder> {
    T builder();
}
