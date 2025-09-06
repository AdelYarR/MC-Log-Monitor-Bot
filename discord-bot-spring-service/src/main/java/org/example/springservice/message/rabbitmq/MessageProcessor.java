package org.example.springservice.message.rabbitmq;

import jakarta.transaction.Transactional;
import org.example.springservice.message.rabbitmq.utils.MessageParser;
import org.example.springservice.models.User;
import org.example.springservice.repository.UserRepository;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Component
public class MessageProcessor {

    private final UserRepository userRepository;
    private final MessageParser messageParser;

    public MessageProcessor(UserRepository userRepository, MessageParser messageParser) {
        this.userRepository = userRepository;
        this.messageParser = messageParser;
    }

    @Transactional
    public void processMessage(String message) {
        if (message.contains("UUID")) {
            handleUuidMessage(message);
        }
        else if (message.contains("joined")) {
            handleJoinedMessage(message);
        }
        else if (message.contains("left")) {
            handleLeftMessage(message);
        }
        else if (message.contains("advancement")) {
            handleAdvancementMessage(message);
        }
    }

    private void handleUuidMessage(String message) {
        String uuid = messageParser.parseUUID(message);
        String username = messageParser.parseUsername(message);

        // Проверка есть ли игрок в БД
        if (userRepository.findByMinecraftUUID(UUID.fromString(uuid)).isEmpty()) {
            User user = new User(UUID.fromString(uuid), username);
            userRepository.save(user);
        }

        // Проверка сменил ли игрок свой никнейм,
        // если да, то меняем его в БД
        User user = userRepository.findByMinecraftUUID(UUID.fromString(uuid)).get();
        String usernameFromDB = user.getUsername();
        if (!usernameFromDB.equals(username)) {
            user.setUsername(username);
        }
    }

    private void handleJoinedMessage(String message) {
        String username = messageParser.joinedParse(message);
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isEmpty()) {
            throw new RuntimeException("User is not found by username");
        }

        user.get().setOnlineStatus(true);
    }

    private void handleLeftMessage(String message) {
        String username = messageParser.leftParse(message);
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isEmpty()) {
            throw new RuntimeException("User is not found by username");
        }

        user.get().setOnlineStatus(false);
        user.get().setLastOnline(new Timestamp(new Date().getTime()));
    }

    private void handleAdvancementMessage(String message) {
        String username = messageParser.achievementParse(message);
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isEmpty()) {
            throw new RuntimeException("User is not found by username");
        }

        user.get().increaseAchievementsCompleted();
    }
}