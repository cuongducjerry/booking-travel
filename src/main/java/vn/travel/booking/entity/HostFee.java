package vn.travel.booking.entity;

import jakarta.persistence.*;
import lombok.Data;
import vn.travel.booking.util.constant.FeeStatus;

import java.time.Instant;

@Entity
@Table(name = "host_fees")
@Data
public class HostFee {

    @Id
    @GeneratedValue
    private Long id;

    private Long hostId;
    private Long bookingId;

    private double amount;   // fee money
    private double rate;     // % commission

    @Enumerated(EnumType.STRING)
    private FeeStatus status; // PENDING, PAID, OVERDUE

    private Instant dueAt;
    private Instant paidAt;
}
