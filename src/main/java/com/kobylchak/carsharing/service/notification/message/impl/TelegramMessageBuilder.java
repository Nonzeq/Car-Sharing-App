package com.kobylchak.carsharing.service.notification.message.impl;

import com.kobylchak.carsharing.service.notification.message.MessageBuilder;

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
        return new TelegramMessageBuilder.TelegramListItemBuilder();
    }
    
    @Override
    public MessageBuilder newLine() {
        sb.append("\n");
        return this;
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
            sb.append("<b>")
                    .append(itemCount++)
                    .append(") ")
                    .append("</b>")
                    .append(item)
                    .append("\n");
            return this;
        }
        
        @Override
        public TelegramMessageBuilder buildItemsList() {
            TelegramMessageBuilder.this.sb.append(sb);
            return TelegramMessageBuilder.this;
        }
    }
}
