package org.example.message.utils;

public class MessageParser {

    public static String parseUUID(String message) {
        String[] parts = message.split(" ");
        return parts[9];
    }

    public static String parseUsername(String message) {
        String[] parts = message.split(" ");
        return parts[7];
    }

    public static String joinedParse(String message) {
        String[] parts = message.split(" ");
        return parts[3];
    }

    public static String leftParse(String message) {
        String[] parts = message.split(" ");
        return parts[3];
    }

    public static String achievementParse(String message) {
        String[] parts = message.split(" ");
        return parts[3];
    }
}
