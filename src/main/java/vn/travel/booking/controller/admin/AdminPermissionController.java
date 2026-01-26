package vn.travel.booking.controller.admin;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vn.travel.booking.dto.response.permission.ResPermissionDTO;
import vn.travel.booking.dto.response.ResultPaginationDTO;
import vn.travel.booking.entity.Permission;
import vn.travel.booking.service.PermissionService;
import vn.travel.booking.specification.PermissionSpecification;
import vn.travel.booking.util.annotation.ApiMessage;

// superadmin
@RestController
@RequestMapping("/api/v1/admin")
public class AdminPermissionController {

    private final PermissionService permissionService;

    public AdminPermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    /**
     * Get all permissions
     * SUPER_ADMIN / ADMIN (read-only)
     */

    @GetMapping("/permissions")
    @PreAuthorize("hasAuthority('PERMISSION_LIST_ALL')")
    @ApiMessage("Fetch all permission")
    public ResponseEntity<ResultPaginationDTO> getAllPermissions(@RequestParam(required = false) String keyword, Pageable pageable) {

        Specification<Permission> spec = Specification
                .where(PermissionSpecification.keyword(keyword));

        ResultPaginationDTO res = this.permissionService.handleListPermission(spec, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    /**
     * Get permission by id
     */
    @GetMapping("/permissions/{id}")
    @PreAuthorize("hasAuthority('PERMISSION_VIEW')")
    @ApiMessage("Fetch permission by id")
    public ResponseEntity<ResPermissionDTO> getPermissionById(@PathVariable Long id) {
        return ResponseEntity.ok(permissionService.getPermissionById(id));
    }

}
