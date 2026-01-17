package vn.travel.booking.mapper;

import org.springframework.stereotype.Component;
import vn.travel.booking.dto.response.permission.ResPermissionDTO;
import vn.travel.booking.entity.Permission;

@Component
public class PermissionMapper {
    public ResPermissionDTO convertToResPermissionDTO(Permission permission) {
        ResPermissionDTO res = new ResPermissionDTO();
        res.setId(permission.getId());
        res.setCode(permission.getCode());
        return res;
    }
}
