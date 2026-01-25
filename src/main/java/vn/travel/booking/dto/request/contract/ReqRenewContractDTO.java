package vn.travel.booking.dto.request.contract;

import lombok.Data;

import java.time.Instant;
import java.time.LocalDate;

@Data
public class ReqRenewContractDTO {
    private LocalDate newEndDate;
    private double expectedCommissionRate;
}
