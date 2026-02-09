package vn.travel.booking.mapper;

import org.springframework.stereotype.Component;
import vn.travel.booking.dto.response.notification.ResNotiDTO;
import vn.travel.booking.entity.Notification;

@Component
public class NotificationMapper {

    public static ResNotiDTO toResDTO(Notification n) {
        return ResNotiDTO.builder()
                .id(n.getId())
                .title(n.getTitle())
                .content(n.getContent())
                .type(n.getType())
                .isRead(n.isRead())
                .createdAt(n.getCreatedAt())
                .build();
    }

}
