package com.projet1.client.views.chat;

import com.projet1.shared.model.Message;

public  class MessageFormatter {
    
    public static String formatMessage(String senderName, Message content) {
        String dateHour = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm")
        .format(new java.util.Date(Long.parseLong(content.getTimestamp()) * 1000));
            return "[" + dateHour + "] " + senderName + ": " + content.getContent() + "\n";
    }
}
