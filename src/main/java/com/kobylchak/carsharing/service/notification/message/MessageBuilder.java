package com.kobylchak.carsharing.service.notification.message;

public interface MessageBuilder {
    MessageBuilder title(String title);
    ListItemBuilder listItems();
    MessageBuilder newLine();
    String build();
    
    interface ListItemBuilder {
        ListItemBuilder item(String item);
        MessageBuilder buildItemsList();
    }
}
