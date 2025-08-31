package org.example.message_sender;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class MessageSender {
    private static final Logger logger = LoggerFactory.getLogger(MessageSender.class);

    private final static String QUEUE_NAME = "server-logs-queue";

    private final String host;
    private Connection connection;
    private Channel channel;

    public MessageSender(String host) {
        this.host = host;
    }

    public void initialize() {
        // Подключение к серверу
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(host);

        try {
            connection = factory.newConnection();
            channel = connection.createChannel();
//            channel.queueDeclarePassive(QUEUE_NAME);
            channel.queueDeclare(
                    "server-logs-queue",
                    true,
                    false,
                    false,
                    null
            );
            logger.info("Connected to RabbitMQ and found queue: " + QUEUE_NAME);
        } catch (IOException | TimeoutException err) {
            throw new RuntimeException("Failed to initialize message sender: " + err.getMessage());
        }
    }

    public void sendMessage(String message) {
        try {
            channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
            logger.info("Sent message to RabbitMQ queue: " + message);
        } catch (IOException err) {
            throw new RuntimeException(err);
        }
    }
}
