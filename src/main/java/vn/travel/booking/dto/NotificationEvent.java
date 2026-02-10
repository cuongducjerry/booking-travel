package vn.travel.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.travel.booking.util.constant.NotificationType;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationEvent {

    private String eventId;

    // RECEIVER
    private Long userId;

    private NotificationType type;
    private String title;
    private String content;

    // Use only during processing.
    private boolean sendEmail;
}
