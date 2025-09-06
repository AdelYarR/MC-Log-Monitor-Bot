package org.example.springservice.config;
import lombok.Data;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "spring.rabbitmq")
@Data
public class RabbitMQConfig {

    private String host;
    private int port;
    private String queueName;

    @Bean
    CachingConnectionFactory connectionFactory() {
        return new CachingConnectionFactory(host);
    }

    @Bean
    Queue queue() {
        return new Queue(queueName);
    }

    @Bean
    AmqpAdmin amqpAdmin(CachingConnectionFactory connectionFactory, Queue queue) {
        AmqpAdmin admin = new RabbitAdmin(connectionFactory);
        admin.declareQueue(queue);
        return admin;
    }

    @Bean
    AmqpTemplate amqpTemplate(CachingConnectionFactory connectionFactory) {
        return new RabbitTemplate(connectionFactory);
    }
}
