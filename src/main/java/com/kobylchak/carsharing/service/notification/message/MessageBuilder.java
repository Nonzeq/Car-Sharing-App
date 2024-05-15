package com.kobylchak.carsharing.service.notification.message;

public interface MessageBuilder {
    MessageBuilder title(String title);
    
    ListItemBuilder listItems();
    
    String build();
    
    interface ListItemBuilder {
        ListItemBuilder item(String item);
        
        MessageBuilder buildItemsList();
    }
}
