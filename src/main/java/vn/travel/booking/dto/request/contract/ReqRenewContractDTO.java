package vn.travel.booking.dto.request.contract;

import lombok.Data;

import java.time.Instant;

@Data
public class ReqRenewContractDTO {
    private Instant newEndDate;
    private double expectedCommissionRate;
}
