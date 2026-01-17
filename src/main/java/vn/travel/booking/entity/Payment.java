package vn.travel.booking.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import vn.travel.booking.util.constant.PaymentStatus;

import java.time.Instant;

@Entity
@Table(name = "payments")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String paymentMethod;
    private double amount;
    private PaymentStatus status; // PENDING, SUCCESS, FAILED
    private String providerTxnId; // Transaction code from VNPay/Stripe
    private String currency;      // VND, USD

    @Builder.Default
    private boolean active = true;

    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;
    private String updatedBy;

    @ManyToOne
    @JoinColumn(name = "booking_id")
    @JsonIgnore
    private Booking booking;

    @PrePersist
    public void prePersist() { this.createdAt = Instant.now(); }

    @PreUpdate
    public void preUpdate() { this.updatedAt = Instant.now(); }
}