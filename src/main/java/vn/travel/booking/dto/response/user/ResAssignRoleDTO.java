package vn.travel.booking.dto.response.user;

import lombok.Data;

import java.time.Instant;

@Data
public class ResAssignRoleDTO {
    private Long idUser;
    private String roleName;
    private Instant updatedAt;
}
