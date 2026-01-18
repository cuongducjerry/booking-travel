package vn.travel.booking.dto.response.user;

import lombok.Data;
import vn.travel.booking.util.constant.StatusUser;

import java.time.Instant;

@Data
public class ResUpdateUserStatusDTO {
    private long userId;
    private StatusUser status;
    private Instant updatedAt;
}
