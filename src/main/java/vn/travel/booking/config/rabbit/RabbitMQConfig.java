package vn.travel.booking.config.rabbit;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Bean
    DirectExchange notificationExchange() {
        return new DirectExchange(RabbitMQConst.NOTI_EXCHANGE);
    }

    @Bean
    Queue notificationQueue() {
        return new Queue(RabbitMQConst.NOTI_QUEUE, true);
    }

    @Bean
    Binding notificationBinding() {
        return BindingBuilder
                .bind(notificationQueue())
                .to(notificationExchange())
                .with(RabbitMQConst.NOTI_ROUTING_KEY);
    }
}

