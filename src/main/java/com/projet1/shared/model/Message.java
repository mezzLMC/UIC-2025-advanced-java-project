package com.projet1.shared.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    private Long id;
    private Long senderId;
    private Long receiverId;
    private String content;
    private String timestamp;
    private Boolean receiverRead;

    public static Message fromBinary(String rawMessage) {
        String[] parts = rawMessage.split(",", 6);

        if (parts.length < 6) return null;
        Message msg = Message.builder()
            .id(parts[0].equals("null") ? null : Long.parseLong(parts[0]))
            .senderId(Long.parseLong(parts[1]))
            .receiverId(Long.parseLong(parts[2]))
            .timestamp(parts[3])
            .content(parts[4])
            .receiverRead(Boolean.parseBoolean(parts[5]))
            .build();
        return msg;
    }

    public String toBinary() {
        return id + "," + senderId + "," + receiverId + "," + timestamp + "," + content + "," + receiverRead;
    }
}
