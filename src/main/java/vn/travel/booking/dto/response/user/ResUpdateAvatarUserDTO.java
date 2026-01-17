package vn.travel.booking.dto.response.user;

import lombok.Data;

import java.time.Instant;

@Data
public class ResUpdateAvatarUserDTO {
    private String urlImage;
    private long userId;
    private Instant updatedAt;
}
