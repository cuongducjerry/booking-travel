package vn.travel.booking.dto.response.role;

import lombok.*;
import vn.travel.booking.dto.response.permission.ResPermissionDTO;

import java.time.Instant;
import java.util.List;

@Data
public class ResRoleDTO {
    private long id;
    private String name;
    private String description;
    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;
    private String updatedBy;
    private List<ResPermissionDTO> permissions;
}
