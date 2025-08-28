package org.example.message;

import org.example.message.utils.MessageParser;
import org.example.repository.UserRepository;

import java.util.Date;
import java.sql.Timestamp;

public class MessageProcessor {

    private UserRepository userRepository;

    public MessageProcessor(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void processMessage(String message) {
        if (message.contains("UUID")) {
            String uuid = MessageParser.parseUUID(message);
            String username = MessageParser.parseUsername(message);

            // Проверка есть ли игрок в БД
            if (!userRepository.userExists(uuid)) {
                userRepository.addUser(uuid, username);
            }

            // Проверка сменил ли игрок свой никнейм,
            // если да, то меняем его в БД
            String usernameFromDB = userRepository.getUsernameByUUID(uuid);
            if (!usernameFromDB.equals(username)) {
                userRepository.updateUsername(uuid, username);
            }
        }
        if (message.contains("joined")) {
            String username = MessageParser.joinedParse(message);
            userRepository.updateOnlineStatus(username, true);
        }
        if (message.contains("left")) {
            String username = MessageParser.leftParse(message);
            userRepository.updateOnlineStatus(username, false);
            userRepository.updateLastTimeOnline(username, new Timestamp(new Date().getTime()));
        }
        if (message.contains("advancement")) {
            String username = MessageParser.achievementParse(message);
            userRepository.updateAchievementsCompleted(username);
        }
    }
}
