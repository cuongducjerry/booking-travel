package vn.travel.booking.mapper;

import org.springframework.stereotype.Component;
import vn.travel.booking.dto.response.permission.ResPermissionDTO;
import vn.travel.booking.dto.response.role.ResRoleDTO;
import vn.travel.booking.entity.Role;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class RoleMapper {

    private final PermissionMapper permissionMapper;

    public RoleMapper(PermissionMapper permissionMapper) {
        this.permissionMapper = permissionMapper;
    }

    public ResRoleDTO convertResRoleDTO(Role role) {
        ResRoleDTO roleDTO = new ResRoleDTO();
        roleDTO.setId(role.getId());
        roleDTO.setName(role.getName());
        roleDTO.setDescription(role.getDescription());
        roleDTO.setCreatedAt(role.getCreatedAt());
        roleDTO.setUpdatedAt(role.getUpdatedAt());
        roleDTO.setCreatedBy(role.getCreatedBy());
        roleDTO.setUpdatedBy(role.getUpdatedBy());

        List<ResPermissionDTO> listPermissionDTO = role.getPermissions().stream()
                .map(item -> this.permissionMapper.convertToResPermissionDTO(item))
                .collect(Collectors.toList());

        roleDTO.setPermissions(listPermissionDTO);
        return roleDTO;
    }

}
