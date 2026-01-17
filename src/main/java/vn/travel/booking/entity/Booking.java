package vn.travel.booking.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "bookings")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private LocalDate checkIn;
    private LocalDate checkOut;
    private double pricePerNightSnapshot;
    private int nights;

    private double grossAmount;      // total amount paid by customers
    private double commissionRate;   // snapshot at the time of booking
    private double commissionFee;
    private double hostEarning;

    private String status; // NEW, CONFIRMED, CANCELLED, DONE

    @Builder.Default
    private boolean active = true;

    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;
    private String updatedBy;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "property_id")
    private Property property;

    @OneToMany(mappedBy = "booking", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Payment> payments;

    @OneToMany(mappedBy = "booking", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Message> messages;

    @PrePersist
    public void prePersist() { this.createdAt = Instant.now(); }

    @PreUpdate
    public void preUpdate() { this.updatedAt = Instant.now(); }
}
