package vn.travel.booking.dto.response.payout;

import lombok.Builder;
import lombok.Data;
import vn.travel.booking.util.constant.PayoutStatus;
import java.time.LocalDate;

@Data
@Builder
public class ResHostPayoutDTO {
    private Long id;
    private Long hostId;
    private Long contractId;
    private LocalDate periodFrom;
    private LocalDate periodTo;
    private double grossAmount;
    private double commissionFee;
    private double netAmount;
    private PayoutStatus status;
}

