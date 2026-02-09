package vn.travel.booking.controller.notification;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import vn.travel.booking.dto.response.notification.ResNotiDTO;
import vn.travel.booking.entity.Notification;
import vn.travel.booking.mapper.NotificationMapper;
import vn.travel.booking.repository.NotificationRepository;
import vn.travel.booking.service.NotificationUserService;
import vn.travel.booking.service.notification.NotificationCacheService;
import vn.travel.booking.util.SecurityUtil;
import vn.travel.booking.util.annotation.ApiMessage;
import vn.travel.booking.util.constant.NotificationType;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationUserService notificationUserService;

    @GetMapping
    @ApiMessage("Get my notifications")
    public ResponseEntity<List<ResNotiDTO>> listNotifications() {
        return ResponseEntity.ok(notificationUserService.getMyNotifications());
    }

    @GetMapping("/unread")
    @ApiMessage("Get total unread notifications")
    public ResponseEntity<Long> unreadNotifications() {
        return ResponseEntity.ok(notificationUserService.getTotalUnread());
    }

    @PostMapping("/read-all")
    @ApiMessage("Mark all notifications as read")
    public ResponseEntity<Void> readAllNotifications() {
        notificationUserService.readAll();
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/read")
    @ApiMessage("Mark one notification as read")
    public ResponseEntity<Void> readOne(@PathVariable Long id) {
        notificationUserService.readOne(id);
        return ResponseEntity.ok().build();
    }
}

