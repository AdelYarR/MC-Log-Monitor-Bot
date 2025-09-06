package org.example.springservice.message.rabbitmq.utils;

import org.springframework.stereotype.Component;

@Component
public class MessageParser {

    public String parseUUID(String message) {
        String[] parts = message.split(" ");
        return parts.length > 9 ? parts[9]: "";
    }

    public String parseUsername(String message) {
        String[] parts = message.split(" ");
        return parts.length > 7 ? parts[7]: "";
    }

    public String joinedParse(String message) {
        String[] parts = message.split(" ");
        return parts.length > 3 ? parts[3]: "";
    }

    public String leftParse(String message) {
        String[] parts = message.split(" ");
        return parts.length > 3 ? parts[3]: "";
    }

    public String achievementParse(String message) {
        String[] parts = message.split(" ");
        return parts.length > 3 ? parts[3]: "";
    }
}
