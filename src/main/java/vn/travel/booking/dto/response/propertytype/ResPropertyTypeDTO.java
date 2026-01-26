package vn.travel.booking.dto.response.propertytype;

import lombok.Data;

import java.time.Instant;

@Data
public class ResPropertyTypeDTO {
    private Long id;
    private String name;
    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;
    private String updatedBy;
}
