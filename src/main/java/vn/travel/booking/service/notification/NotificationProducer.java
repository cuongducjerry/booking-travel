package vn.travel.booking.service.notification;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import vn.travel.booking.config.rabbit.RabbitMQConst;
import vn.travel.booking.dto.NotificationEvent;

@Service
@RequiredArgsConstructor
public class NotificationProducer {

    private final RabbitTemplate rabbitTemplate;

    public void send(NotificationEvent event) {
        rabbitTemplate.convertAndSend(
                RabbitMQConst.NOTI_EXCHANGE,
                RabbitMQConst.NOTI_ROUTING_KEY,
                event
        );
    }
}

