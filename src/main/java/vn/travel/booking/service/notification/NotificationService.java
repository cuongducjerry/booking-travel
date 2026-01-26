package vn.travel.booking.service.notification;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.travel.booking.dto.NotificationEvent;
import vn.travel.booking.util.constant.NotificationType;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationProducer producer;

    public void notify(
            Long userId,
            NotificationType type,
            String title,
            String content,
            boolean sendEmail
    ) {
        producer.send(new NotificationEvent(
                userId,
                type,
                title,
                content,
                sendEmail
        ));
    }
}

