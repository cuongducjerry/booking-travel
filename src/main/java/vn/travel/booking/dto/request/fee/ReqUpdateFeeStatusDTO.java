package vn.travel.booking.dto.request.fee;

import lombok.Data;
import vn.travel.booking.util.constant.FeeStatus;

@Data
public class ReqUpdateFeeStatusDTO {
    private FeeStatus status; // PAID | OVERDUE
}
