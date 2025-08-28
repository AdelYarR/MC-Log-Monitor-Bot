package org.example.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.LinkedHashMap;
import java.util.UUID;

public class UserRepository {
    private static final Logger logger = LoggerFactory.getLogger(UserRepository.class);

    private Connection connection;

    public UserRepository(Connection connection) {
        this.connection = connection;
    }

    public boolean userExists(String uuid) {
        String sql = "SELECT EXISTS (SELECT 1 FROM users WHERE minecraft_uuid = ?)";
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setObject(1, UUID.fromString(uuid));
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getBoolean(1);
            }
            return false;
        } catch (SQLException err) {
            throw new RuntimeException("Failed to check if user is new: " + err.getMessage());
        }
    }

    public void addUser(String uuid, String username) {
        String sql = "INSERT INTO users (minecraft_uuid, username) VALUES (?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql);) {
            statement.setObject(1, UUID.fromString(uuid));
            statement.setObject(2, username);

            statement.executeUpdate();
            logger.info("Inserted into users table " + uuid + " " + username);
        } catch (SQLException err) {
            throw new RuntimeException("Failed to add new user: " + err.getMessage());
        }
    }

    public String getUsernameByUUID(String uuid) {
        String sql = "SELECT username FROM users WHERE minecraft_uuid = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql);) {
            statement.setObject(1, UUID.fromString(uuid));

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString(1);
            }
            throw new RuntimeException("Failed to get username by UUID");
        } catch (SQLException err) {
            throw new RuntimeException("Failed to get username by UUID: " + err.getMessage());
        }
    }

    public void updateUsername(String uuid, String username) {
        String sql = "UPDATE users SET username = ? WHERE minecraft_uuid = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql);) {
            statement.setObject(1, username);
            statement.setObject(2, UUID.fromString(uuid));

            statement.executeUpdate();
        } catch (SQLException err) {
            throw new RuntimeException("Failed to update username by UUID: " + err.getMessage());
        }
    }

    public void updateOnlineStatus(String username, boolean status) {
        String sql = "UPDATE users SET online_status = ? WHERE username = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql);) {
            statement.setObject(1, status);
            statement.setObject(2, username);

            statement.executeUpdate();
        } catch (SQLException err) {
            throw new RuntimeException("Failed to update player's online status: " + err.getMessage());
        }
    }

    public void updateLastTimeOnline(String username, Timestamp timestamp) {
        
    }

    public void updateAchievementsCompleted(String username) {
        String sql = "UPDATE users SET achievements_completed = achievements_completed + 1 WHERE username = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql);) {
            statement.setObject(1, username);

            statement.executeUpdate();
        } catch (SQLException err) {
            throw new RuntimeException("Failed to update player's amount of completed achievements: " + err.getMessage());
        }
    }

    public int getPlayersOnline() {
        String sql = "SELECT COUNT(*) FROM users WHERE online_status = true";
        try (Statement statement = connection.createStatement();) {
            ResultSet resultSet = statement.executeQuery(sql);
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
            throw new RuntimeException("Failed to count players online");
        } catch (SQLException err) {
            throw new RuntimeException("Failed to count players online: " + err.getMessage());
        }
    }

    public LinkedHashMap<String, Integer> getTopPlayersByAchievements() {
        LinkedHashMap<String, Integer> map = new LinkedHashMap<>();

        String sql = "SELECT username, achievements_completed FROM users ORDER BY achievements_completed DESC LIMIT 5";
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                String username = resultSet.getString(1);
                Integer achievements_completed = resultSet.getInt(2);

                if (username != null || achievements_completed != null) {
                    map.put(username, achievements_completed);
                }
            }

            return map;
        } catch (SQLException err) {
            throw new RuntimeException("Failed to find top 5 players by achievements: " + err.getMessage());
        }
    }
}
