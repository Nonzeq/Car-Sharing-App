package com.kobylchak.carsharing.service.notification;

public interface NotificationService<T> {
    void sendNotification(T value);
}
