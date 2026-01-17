package vn.travel.booking.dto.request.role;

import lombok.Data;

import java.util.List;

@Data
public class ReqAssignPermissionDTO {
    private List<Long> permissionIds;
}
