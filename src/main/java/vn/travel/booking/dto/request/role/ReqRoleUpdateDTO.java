package vn.travel.booking.dto.request.role;

import lombok.Data;

import java.time.Instant;

@Data
public class ReqRoleUpdateDTO {
    private long id;
    private String name;
    private String description;
    private Instant updatedAt;
}
