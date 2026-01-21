package vn.travel.booking.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import vn.travel.booking.util.constant.DraftAction;

import java.time.Instant;

@Entity
@Table(name = "property_image_drafts")
@Getter
@Setter
public class PropertyImageDraft {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String imageUrl;

    @Enumerated(EnumType.STRING)
    private DraftAction action; // ADD | DELETE

    @ManyToOne
    @JoinColumn(name = "property_id")
    private Property property;

    private Instant createdAt = Instant.now();
}
