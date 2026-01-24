package vn.travel.booking.dto.request.contract;

import lombok.Data;

import java.time.Instant;

@Data
public class ReqHostContractRequestDTO {
    private double expectedCommissionRate; // suggested host
    private Instant startDate;              // desired host
    private Instant endDate;
}
