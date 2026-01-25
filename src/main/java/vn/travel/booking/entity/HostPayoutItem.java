package vn.travel.booking.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "host_payout_items",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"booking_id"})
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HostPayoutItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Each booking appears only once in this table.
    @OneToOne
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @ManyToOne
    @JoinColumn(name = "payout_id", nullable = false)
    private HostPayout payout;

    private double bookingAmount;
    private double commissionFee;
    private double netAmount;
}

