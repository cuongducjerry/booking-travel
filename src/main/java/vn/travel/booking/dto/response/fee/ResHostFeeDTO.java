package vn.travel.booking.dto.response.fee;

import lombok.Data;
import vn.travel.booking.util.constant.FeeStatus;

import java.time.Instant;

@Data
public class ResHostFeeDTO {

    private Long id;
    private Long bookingId;

    private double amount;
    private double rate;
    private FeeStatus status;

    private Instant dueAt;
    private Instant paidAt;
}

