package vn.travel.booking.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResHostDashboardDTO {

    private long countProperty;
    private long countBooking;
    private long countPayout;

    private double totalGross;
    private double totalCommission;
    private double totalNet;

}
