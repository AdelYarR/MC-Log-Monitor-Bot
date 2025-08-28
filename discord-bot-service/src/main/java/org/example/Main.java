package org.example;

import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.example.bot.Bot;
import org.example.repository.UserRepository;
import org.example.message.MessageReceiver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class Main {
    public static void main(String[] args) {
        // Создание логгера
        Logger logger = LoggerFactory.getLogger(Main.class);

        // Создание экземпляра класса для считывания данных из .env файла
        Dotenv dotenv = Dotenv.load();
        String token = dotenv.get("DISCORD_TOKEN");
        String rabbitMQHost = dotenv.get("RABBITMQ_HOST");
        String JDBCUrl = dotenv.get("JDBC_CONNECTION_URL");
        String DBUser = dotenv.get("DB_USER");
        String DBPassword = dotenv.get("DB_PASSWORD");

        // Подключение к БД
        Connection DBConnection = null;
        try {
            DBConnection = DriverManager.getConnection(JDBCUrl, DBUser, DBPassword);
            logger.info("Connected to PostgreSQL: " + DBConnection);
            UserRepository userRepository = new UserRepository(DBConnection);

            // Создание счётчика действий, чтобы Main поток сразу не завершил работу
            CountDownLatch latch = new CountDownLatch(1);

            // Создание экземпляра класса для получения сообщений из брокера сообщений
            MessageReceiver messageReceiver = new MessageReceiver(rabbitMQHost, userRepository);
            messageReceiver.initialize();
            messageReceiver.receiveMessage();

            // Создание экземпляра класса дискорд-бота
            Bot bot = new Bot(userRepository);

            // Для соединения с Discord нужно создать сущность JDA
            JDABuilder.createLight(token)
                    .addEventListeners(bot)
                    .enableIntents(
                            List.of(
                                    GatewayIntent.GUILD_MESSAGES,
                                    GatewayIntent.MESSAGE_CONTENT
                            )
                    )
                    .setStatus(OnlineStatus.ONLINE)
                    .setActivity(Activity.watching("Minecraft"))
                    .build();

            latch.await();
        } catch (SQLException err) {
            throw new IllegalStateException("Failed to connect to PostgreSQL: " + err.getMessage());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            if (DBConnection != null) {
                try {
                    DBConnection.close();
                } catch (SQLException err) {
                    throw new IllegalStateException("Failed to close database connection: " + err.getMessage());
                }
            }
        }
    }
}