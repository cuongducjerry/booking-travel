package vn.travel.booking.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResDashboardDTO {
    private long countUser;
    private long countProperty;
    private long countBooking;
    private long countContract;
    private long countPayout;

    private double totalGross;       // total amount paid by customers
    private double totalCommission;  // system fees
    private double totalNet;         // host payment
}
