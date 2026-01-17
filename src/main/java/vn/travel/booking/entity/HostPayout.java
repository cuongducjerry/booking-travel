package vn.travel.booking.entity;

import jakarta.persistence.*;
import lombok.*;
import vn.travel.booking.util.constant.PayoutStatus;

import java.time.Instant;

@Entity
@Table(name = "host_payouts")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HostPayout {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private Instant periodFrom;
    private Instant periodTo;

    private double grossAmount;   // total booking amount
    private double commissionFee; // platform fees
    private double netAmount;      // money the host receives

    @Enumerated(EnumType.STRING)
    private PayoutStatus status;
    // PENDING, PAID, FAILED, HOLD

    private Instant paidAt;
    private String transactionRef;

    @ManyToOne
    @JoinColumn(name = "host_id")
    private User host;

    @ManyToOne
    @JoinColumn(name = "contract_id")
    private HostContract contract;

}

