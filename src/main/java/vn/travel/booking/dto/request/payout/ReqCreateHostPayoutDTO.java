package vn.travel.booking.dto.request.payout;

import lombok.Data;

import java.time.Instant;
import java.time.LocalDate;

@Data
public class ReqCreateHostPayoutDTO {
    private Long hostId;
    private Long contractId;
    private LocalDate periodFrom;
    private LocalDate periodTo;
}

