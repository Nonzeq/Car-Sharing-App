package com.kobylchak.carsharing.service.notification.impl;

import com.kobylchak.carsharing.exception.NotificationExcetion;
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
public class TelegramNotificationService implements NotificationService {
    @Value("${telegram.chat.id}")
    private String chatId;
    private final TelegramBot telegramBot;
    
    @Override
    public void sendNotification(String text) {
        SendMessage req = new SendMessage(chatId, text)
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
}
