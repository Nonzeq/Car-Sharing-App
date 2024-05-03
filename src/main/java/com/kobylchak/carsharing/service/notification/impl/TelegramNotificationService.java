package com.kobylchak.carsharing.service.notification.impl;

import com.kobylchak.carsharing.exception.NotificationExcetion;
import com.kobylchak.carsharing.model.Rental;
import com.kobylchak.carsharing.service.notification.NotificationService;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TelegramNotificationService implements NotificationService<Rental> {
    @Value("${telegram.chat.id}")
    private String chatId;
    private final TelegramBot telegramBot;
    
    @Override
    public void sendNotification(Rental rental) {
        SendMessage req = new SendMessage(chatId, prepareMessage(rental))
                                  .parseMode(ParseMode.HTML)
                                  .disableWebPagePreview(true)
                                  .disableNotification(true)
                                  .replyToMessageId(1);
        SendResponse response = telegramBot.execute(req);
        if (!response.isOk()) {
            throw new NotificationExcetion("Can't send notification, response: "
                                           + response.message());
        }
    }
    
    private String prepareMessage(Rental rental) {
        return "<b>Created new Rental</b>"
               + System.lineSeparator()
               + "<b>ID</b>: " + rental.getId()
               + System.lineSeparator()
               + "<b>Date</b>: " + rental.getRentalDate()
               + System.lineSeparator()
               + "<b>Car brand</b>: " + rental.getCar().getBrand()
               + System.lineSeparator()
               + "<b>Car model</b>: " + rental.getCar().getModel()
               + System.lineSeparator()
               + "<b>Return date</b>: " + rental.getReturnDate()
               + System.lineSeparator()
               + "<b>User email</b>: " + rental.getUser().getEmail();
    }
}
