package vn.travel.booking.dto.response.notification;

import lombok.*;
import vn.travel.booking.util.constant.NotificationType;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResNotiDTO {
    private Long id;
    private String title;
    private String content;
    private NotificationType type;
    private boolean isRead;
    private Instant createdAt;
}
