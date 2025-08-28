package org.example.message;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import org.example.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class MessageReceiver {
    private static final Logger logger = LoggerFactory.getLogger(MessageReceiver.class);

    private final static String QUEUE_NAME = "server-logs-queue";

    private String host;
    private java.sql.Connection DBConnection;
    private Connection connection;
    private Channel channel;
    private DeliverCallback deliverCallback;
    private MessageProcessor messageProcessor;

    public MessageReceiver(String host, UserRepository userRepository) {
        this.host = host;
        this.DBConnection = DBConnection;
        this.messageProcessor = new MessageProcessor(userRepository);
    }

    public void initialize() {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(host);

        try {
            connection = factory.newConnection();
            channel = connection.createChannel();
            channel.queueDeclarePassive(QUEUE_NAME);
            logger.info("Connected to RabbitMQ and found queue: " + QUEUE_NAME);
        } catch (IOException | TimeoutException err) {
            throw new RuntimeException("Failed to initialize message sender: " + err.getMessage());
        }
    }

    public void receiveMessage() {
        deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            logger.info("Received message: " + message);
            messageProcessor.processMessage(message);
        };

        try {
            channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> {});
        } catch (IOException err) {
            throw new RuntimeException(err);
        }
    }
}
