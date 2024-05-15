package com.kobylchak.carsharing.service.notification.impl;

import com.kobylchak.carsharing.exception.NotificationExcetion;
import com.kobylchak.carsharing.service.notification.NotificationService;
import com.kobylchak.carsharing.service.notification.message.MessageBuilder;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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
    
    @Override
    public MessageBuilder messageBuilder() {
        return new Builder();
    }
    
    private static class Builder implements MessageBuilder {
        private final StringBuilder sb = new StringBuilder();
        
        @Override
        public MessageBuilder title(String title) {
            sb.append("<b>")
                    .append(title)
                    .append("</b>")
                    .append("\n");
            return this;
        }
        
        @Override
        public ListItemBuilder listItems() {
            return new Builder.TelegramListItemBuilder();
        }
        
        @Override
        public String build() {
            return sb.toString();
        }
        
        private class TelegramListItemBuilder implements MessageBuilder.ListItemBuilder {
            private final StringBuilder sb = new StringBuilder();
            private int itemCount = 1;
            
            @Override
            public MessageBuilder.ListItemBuilder item(String item) {
                if (StringUtils.hasText(item)) {
                    sb.append("<b>")
                            .append(itemCount++)
                            .append(") ")
                            .append("</b>")
                            .append(item)
                            .append("\n");
                }
                return this;
            }
            
            @Override
            public Builder buildItemsList() {
                Builder.this.sb.append(sb);
                return Builder.this;
            }
        }
    }
}
