package vn.travel.booking.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import vn.travel.booking.util.constant.PropertyStatus;

import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "properties")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Property {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String address;
    private String city;
    private double pricePerNight;
    private String currency;
    private int maxGuests;

    @Enumerated(EnumType.STRING)
    private PropertyStatus status;

    @Builder.Default
    private boolean active = true;

    private Instant createdAt;
    private Instant updatedAt;

    @CreatedBy
    @Column(updatable = false)
    private String createdBy;

    @LastModifiedBy
    private String updatedBy;

    @ManyToOne
    @JoinColumn(name = "host_id")
    private User host;

    @ManyToOne
    @JoinColumn(name = "contract_id")
    private HostContract contract;

    @ManyToOne
    @JoinColumn(name = "property_type_id")
    private PropertyType propertyType;

    @OneToMany(mappedBy = "property", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonIgnore
    private List<PropertyImage> images;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "property_amenities",
            joinColumns = @JoinColumn(name = "property_id"),
            inverseJoinColumns = @JoinColumn(name = "amenity_id"))
    @JsonIgnoreProperties("properties")
    private List<Amenity> amenities;

    @OneToMany(mappedBy = "property", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Booking> bookings;

    @OneToMany(mappedBy = "property", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Review> reviews;

    @OneToMany(mappedBy = "property", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Wishlist> wishlists;

    @PrePersist
    public void prePersist() { this.createdAt = Instant.now(); }

    @PreUpdate
    public void preUpdate() { this.updatedAt = Instant.now(); }
}
