package vn.travel.booking.dto.request.role;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class ReqAssignPermissionDTO {

    @NotEmpty(message = "permissionIds không được để trống")
    private List<@NotNull(message = "permissionId không hợp lệ") Long> permissionIds;
}

