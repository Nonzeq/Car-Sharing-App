package com.kobylchak.carsharing.service.notification;

import com.kobylchak.carsharing.model.Rental;

public interface NotificationService<T> {
    void sendNotification(T value);
}
