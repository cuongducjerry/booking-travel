package vn.travel.booking.entity;

import jakarta.persistence.*;
import lombok.*;
import vn.travel.booking.util.constant.ContractStatus;

import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "host_contracts")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HostContract {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String contractCode;

    @Enumerated(EnumType.STRING)
    private ContractStatus status;
    // DRAFT, PENDING, ACTIVE, SUSPENDED, EXPIRED, TERMINATED

    private double commissionRate; // 0.15 = 15%

    private Instant startDate;
    private Instant endDate;
    private Instant signedAt;
    private Instant terminatedAt;

    private String terminationReason;

    @Builder.Default
    private boolean active = true;

    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;
    private String updatedBy;

    // 1 contract - 1 host
    @ManyToOne
    @JoinColumn(name = "host_id")
    private User host;

    // 1 contract - N property
    @OneToMany(mappedBy = "contract", fetch = FetchType.LAZY)
    private List<Property> properties;

    @PrePersist
    public void prePersist() { this.createdAt = Instant.now(); }

    @PreUpdate
    public void preUpdate() { this.updatedAt = Instant.now(); }
}
