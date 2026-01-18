package vn.travel.booking.dto.request.user;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import vn.travel.booking.util.constant.StatusUser;

@Data
public class ReqUpdateUserStatusDTO {
    @NotNull
    private StatusUser status;
}
