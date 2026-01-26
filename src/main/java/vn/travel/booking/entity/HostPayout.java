package vn.travel.booking.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import vn.travel.booking.util.constant.PayoutStatus;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "host_payouts")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HostPayout {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate periodFrom;
    private LocalDate periodTo;

    private double grossAmount;   // total booking amount
    private double commissionFee; // platform fees
    private double netAmount;      // money the host receives
    private String currency;      // VND, USD

    @Enumerated(EnumType.STRING)
    private PayoutStatus status;
    // PENDING, PAID, FAILED, HOLD

    private Instant createdAt;
    private Instant updatedAt;

    @CreatedBy
    @Column(updatable = false)
    private String createdBy;

    @LastModifiedBy
    private String updatedBy;

    private Instant paidAt;
    private String transactionRef;

    @ManyToOne
    @JoinColumn(name = "host_id")
    private User host;

    @ManyToOne
    @JoinColumn(name = "contract_id")
    private HostContract contract;

    @OneToMany(mappedBy = "payout", cascade = CascadeType.ALL)
    @Builder.Default
    private List<HostPayoutItem> items = new ArrayList<>();

    @PrePersist
    public void prePersist() { this.createdAt = Instant.now(); }

    @PreUpdate
    public void preUpdate() { this.updatedAt = Instant.now(); }

}

