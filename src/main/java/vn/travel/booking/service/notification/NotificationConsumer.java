package vn.travel.booking.service.notification;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import vn.travel.booking.config.rabbit.RabbitMQConst;
import vn.travel.booking.dto.NotificationEvent;
import vn.travel.booking.dto.response.notification.ResNotiDTO;
import vn.travel.booking.entity.Notification;
import vn.travel.booking.entity.User;
import vn.travel.booking.repository.NotificationRepository;
import vn.travel.booking.repository.UserRepository;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class NotificationConsumer {

    private final NotificationRepository repo;
    private final NotificationCacheService cache;
    private final SimpMessagingTemplate ws;
    private final EmailService emailService;
    private final UserRepository userRepo;

    @RabbitListener(queues = RabbitMQConst.NOTI_QUEUE)
    public void handle(NotificationEvent event) {

        // 1. Save DB
        User user = userRepo.getReferenceById(event.getUserId());

        Notification noti = Notification.builder()
                .title(event.getTitle())
                .content(event.getContent())
                .type(event.getType())
                .user(user)
                .isRead(false)
                .active(true)
                .build();

        repo.saveAndFlush(noti); //  ID + createdAt

        // 2. Redis unread++
        cache.increaseUnread(event.getUserId(), event.getType());

        // 3. Map -> DTO
        ResNotiDTO dto = ResNotiDTO.builder()
                .id(noti.getId())
                .title(noti.getTitle())
                .content(noti.getContent())
                .type(noti.getType())
                .isRead(false)
                .createdAt(noti.getCreatedAt())
                .build();

        // 4. WebSocket push
        System.out.println("[WS] Send NOTI DTO to user " + event.getUserId());

        ws.convertAndSendToUser(
                event.getUserId().toString(),
                "/queue/notifications",
                dto
        );

        // 5. Email (optional)
        if (event.isSendEmail()) {
            String email = userRepo.findEmailById(event.getUserId());
            emailService.send(email, event.getTitle(), event.getContent());
        }
    }
}

