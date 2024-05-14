package com.kobylchak.carsharing.service.notification.message.impl;

import com.kobylchak.carsharing.service.notification.message.MessageBuilder;
import org.springframework.util.StringUtils;

public class TelegramMessageBuilder implements MessageBuilder {
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
        return new TelegramMessageBuilder.ListItemBuilder();
    }
    
    @Override
    public String build() {
        return sb.toString();
    }
    
    private class ListItemBuilder implements MessageBuilder.ListItemBuilder {
        private final StringBuilder sb = new StringBuilder();
        private int itemCount = 1;
        
        @Override
        public MessageBuilder.ListItemBuilder item(String item) {
            if (StringUtils.hasText(item)){
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
        public TelegramMessageBuilder buildItemsList() {
            TelegramMessageBuilder.this.sb.append(sb);
            return TelegramMessageBuilder.this;
        }
    }
}
