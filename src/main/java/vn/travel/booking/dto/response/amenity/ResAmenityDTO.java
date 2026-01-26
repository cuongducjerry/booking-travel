package vn.travel.booking.dto.response.amenity;

import lombok.Data;

import java.time.Instant;

@Data
public class ResAmenityDTO {
    private Long id;
    private String name;
    private String icon;
    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;
    private String updatedBy;
}
