package vn.travel.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.travel.booking.dto.response.notification.ResNotiDTO;
import vn.travel.booking.entity.Notification;
import vn.travel.booking.mapper.NotificationMapper;
import vn.travel.booking.repository.NotificationRepository;
import vn.travel.booking.service.notification.NotificationCacheService;
import vn.travel.booking.util.SecurityUtil;
import vn.travel.booking.util.constant.NotificationType;
import vn.travel.booking.util.error.BusinessException;
import vn.travel.booking.util.error.ForbiddenException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationUserService {

    private final NotificationRepository repo;
    private final NotificationCacheService cache;

    public List<ResNotiDTO> getMyNotifications() {

        Long userId = SecurityUtil.getCurrentUserId();

        return repo.findByUserIdAndActiveTrueOrderByCreatedAtDesc(userId)
                .stream()
                .map(NotificationMapper::toResDTO)
                .toList();
    }

    public long getTotalUnread() {

        Long userId = SecurityUtil.getCurrentUserId();

        if (userId == null) return 0;

        Long total = cache.getTotalUnread(userId);

        return total != null ? total : 0;
    }

    @Transactional
    public void readAll() {

        Long userId = SecurityUtil.getCurrentUserId();

        repo.markAllAsRead(userId);

        for (NotificationType t : NotificationType.values()) {
            cache.resetUnread(userId, t);
        }
    }

    @Transactional
    public void readOne(Long notiId) {

        Long userId = SecurityUtil.getCurrentUserId();

        Notification noti = repo.findById(notiId)
                .orElseThrow(() -> new BusinessException("Notification not found"));

        if (!noti.getUser().getId().equals(userId)) {
            throw new ForbiddenException("Forbidden");
        }

        if (!noti.isRead()) {
            noti.setRead(true);
            repo.save(noti);
            cache.decreaseUnread(userId, noti.getType());
        }
    }
}

