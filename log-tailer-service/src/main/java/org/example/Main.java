package org.example;

import io.github.cdimascio.dotenv.Dotenv;
import org.example.log_tailer.LogTailer;
import org.example.message_sender.MessageSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;

public class Main {
    public static void main(String[] args) {
        // Создание логгера
        Logger logger = LoggerFactory.getLogger(Main.class);

        // Создание экземпляра класса для считывания данных из .env файла
        Dotenv dotenv = Dotenv.load();

        // Считываем путь до логов сервера и настраиваем задержку считывания в 1 секунду
        String serverLogPath = dotenv.get("SERVER-LOG-PATH");
        String offsetFilePath = "src/main/resources/offset.txt";
        Duration delay = Duration.ofSeconds(1);

        // Создание счётчика действий, чтобы Main поток сразу не завершил работу
        CountDownLatch latch = new CountDownLatch(1);

        // Инициализация очереди брокера сообщений
        String rabbitMQHost = dotenv.get("RABBITMQ-HOST");
        MessageSender messageSender = new MessageSender(rabbitMQHost);
        messageSender.initialize();

        // Создание экземпляра Tailer для обработки логов
        LogTailer tailer = new LogTailer(messageSender, serverLogPath, 500, offsetFilePath);
        tailer.start();

        // Main поток ждёт, пока Tailer поток работает
        try {
            latch.await();
            logger.info("Application has stopped");
        } catch (InterruptedException err) {
            Thread.currentThread().interrupt();
            logger.error("Main thread was interrupted: " + err.getMessage());
        }
    }
}