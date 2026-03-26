package vn.travel.booking.dto.response;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResRevenueByMonthDTO {
    private int year;
    private int month;
    private double totalGross;
    private double totalCommission;
    private double totalNet;
}
