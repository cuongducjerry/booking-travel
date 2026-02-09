package vn.travel.booking.service.notification;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import vn.travel.booking.util.constant.NotificationType;

@Service
@RequiredArgsConstructor
public class NotificationCacheService {

    private final RedisTemplate<String, Object> redis;

    /* =====================
       INCREASE / RESET
       ===================== */

    public void increaseUnread(Long userId, NotificationType type) {
        redis.opsForValue().increment(key(userId, type));
    }

    public void resetUnread(Long userId, NotificationType type) {
        redis.opsForValue().set(key(userId, type), 0);
    }

    public void decreaseUnread(Long userId, NotificationType type) {
        String key = key(userId, type);
        Object val = redis.opsForValue().get(key);

        long current = (val == null) ? 0L : ((Number) val).longValue();

        if (current <= 0) {
            redis.opsForValue().set(key, 0);
        } else {
            redis.opsForValue().set(key, current - 1);
            // or redis.opsForValue().increment(key, -1);
        }
    }

    /* =====================
       GET UNREAD
       ===================== */

    public long getUnread(Long userId, NotificationType type) {
        Object val = redis.opsForValue().get(key(userId, type));
        return val == null ? 0L : Long.parseLong(val.toString());
    }

    public long getTotalUnread(Long userId) {
        return getUnread(userId, NotificationType.BOOKING)
                + getUnread(userId, NotificationType.PAYOUT)
                + getUnread(userId, NotificationType.SYSTEM)
                + getUnread(userId, NotificationType.CONTRACT)
                + getUnread(userId, NotificationType.PROPERTY);
    }

    /* =====================
       KEY BUILDER
       ===================== */

    private String key(Long userId, NotificationType type) {
        return "noti:unread:user:" + userId + ":" + type.name();
    }
}

