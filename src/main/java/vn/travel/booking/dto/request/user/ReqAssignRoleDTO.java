package vn.travel.booking.dto.request.user;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class ReqAssignRoleDTO {
    @NotNull(message = "roleId không được để trống")
    @Positive(message = "roleId phải là số dương")
    private Long roleId;
}
