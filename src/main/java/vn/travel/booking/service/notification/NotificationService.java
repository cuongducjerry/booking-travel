package vn.travel.booking.service.notification;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.travel.booking.dto.NotificationEvent;
import vn.travel.booking.repository.UserRepository;
import vn.travel.booking.util.constant.NotificationType;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationProducer producer;
    private final UserRepository userRepository;

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

    public void notifyAdmins(NotificationType type,
                             String title,
                             String content,
                             boolean sendEmail) {

        List<Long> adminIds = userRepository.findAdminIds();
        // ADMIN + SUPER_ADMIN

        for (Long adminId : adminIds) {
            notify(adminId, type, title, content, sendEmail);
        }
    }
}

