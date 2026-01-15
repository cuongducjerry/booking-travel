package vn.travel.booking.domain.response;

import lombok.Data;

@Data
public class ResUpdateAvatarUserDTO {
    private String urlImage;
    private long userId;
}
