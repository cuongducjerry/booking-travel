package vn.travel.booking.dto.request.contract;

import lombok.Data;

import java.time.Instant;
import java.time.LocalDate;

@Data
public class ReqHostContractRequestDTO {
    private double expectedCommissionRate; // suggested host
    private LocalDate startDate;              // desired host
    private LocalDate endDate;
}
