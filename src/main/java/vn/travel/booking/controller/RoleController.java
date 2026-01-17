package vn.travel.booking.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vn.travel.booking.dto.request.role.ReqAssignPermissionDTO;
import vn.travel.booking.dto.request.role.ReqRoleDTO;
import vn.travel.booking.dto.request.role.ReqRoleUpdateDTO;
import vn.travel.booking.dto.response.ResultPaginationDTO;
import vn.travel.booking.dto.response.role.ResRoleDTO;
import vn.travel.booking.entity.Role;
import vn.travel.booking.service.RoleService;
import vn.travel.booking.specification.RoleSpecification;
import vn.travel.booking.util.annotation.ApiMessage;
import vn.travel.booking.util.error.NameInvalidException;

@RestController
@RequestMapping("/api/v1")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    // Create role
    @PostMapping("/roles")
    @PreAuthorize("hasAuthority('ROLE_CREATE')")
    @ApiMessage("Create a role")
    public ResponseEntity<ResRoleDTO> createRole(@RequestBody ReqRoleDTO dto) throws NameInvalidException {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.roleService.handleCreateRole(dto));
    }

    // Get all roles
    @GetMapping("/roles")
    @PreAuthorize("hasAuthority('ROLE_LIST_ALL')")
    @ApiMessage("Fetch all role")
    public ResponseEntity<ResultPaginationDTO> getAllRole(
            @RequestParam(required = false) String keyword,
            Pageable pageable
    ) {

        Specification<Role> spec = Specification
                .where(RoleSpecification.keyword(keyword));

        return ResponseEntity.status(HttpStatus.OK).body(this.roleService.handleListRole(spec, pageable));
    }

    // Get role detail
    @GetMapping("/roles/{id}")
    @PreAuthorize("hasAuthority('ROLE_VIEW')")
    @ApiMessage("Fetch role by id")
    public ResponseEntity<ResRoleDTO> getRoleById(@PathVariable long id) {
        return ResponseEntity.status(HttpStatus.OK).body(this.roleService.viewRoleById(id));
    }

    // Update role
    @PutMapping("/roles")
    @PreAuthorize("hasAuthority('ROLE_UPDATE')")
    @ApiMessage("Update a role")
    public ResponseEntity<ResRoleDTO> update(@RequestBody ReqRoleUpdateDTO dto) {
        ResRoleDTO resRoleUpdateDTO = this.roleService.handleUpdateRole(dto);
        return ResponseEntity.status(HttpStatus.OK).body(resRoleUpdateDTO);
    }

    // Assign permissions
    @PutMapping("/roles/{id}/permissions")
    @PreAuthorize("hasAuthority('ROLE_ASSIGN_PERMISSION')")
    @ApiMessage("Assign permissions to role")
    public ResponseEntity<ResRoleDTO> assignPermissions(
            @PathVariable long id,
            @RequestBody ReqAssignPermissionDTO permissionIds
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(this.roleService.handleAssignPermissions(id, permissionIds));
    }

    // Delete role
    @DeleteMapping("/roles/{id}")
    @PreAuthorize("hasAuthority('ROLE_DELETE')")
    @ApiMessage("Delete a role")
    public ResponseEntity<Void> deleteRole(@PathVariable long id) {
        this.roleService.handleDeleteRole(id);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }
}
