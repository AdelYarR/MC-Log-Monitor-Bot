package org.example.springservice.message.rabbitmq;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class MessageListener {

    private final MessageProcessor messageProcessor;

    public MessageListener(MessageProcessor messageProcessor) {
        this.messageProcessor = messageProcessor;
    }

    @RabbitListener(queues = "${spring.rabbitmq.queue-name}")
    public void listen(String in) {
        messageProcessor.processMessage(in);
    }
}
