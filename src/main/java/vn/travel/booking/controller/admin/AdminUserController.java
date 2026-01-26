package vn.travel.booking.controller.admin;

import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vn.travel.booking.dto.request.user.ReqAssignRoleDTO;
import vn.travel.booking.dto.request.user.ReqCreateUserDTO;
import vn.travel.booking.dto.request.user.ReqUpdateUserStatusDTO;
import vn.travel.booking.dto.response.ResultPaginationDTO;
import vn.travel.booking.dto.response.user.ResAssignRoleDTO;
import vn.travel.booking.dto.response.user.ResUpdateUserStatusDTO;
import vn.travel.booking.dto.response.user.ResUserDTO;
import vn.travel.booking.entity.User;
import vn.travel.booking.service.UserService;
import vn.travel.booking.specification.UserSpecification;
import vn.travel.booking.util.annotation.ApiMessage;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminUserController {

    private final UserService userService;

    public AdminUserController(UserService userService) {
        this.userService = userService;
    }

    // superadmin, admin
    @PostMapping("/users")
    @PreAuthorize("hasAuthority('USER_CREATE')")
    @ApiMessage("Create a new user")
    public ResponseEntity<ResUserDTO> create(@Valid @RequestBody ReqCreateUserDTO reqUser) {
        ResUserDTO res = this.userService.handleCreateUser(reqUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

    // superadmin, admin
    @DeleteMapping("/users/{id}")
    @PreAuthorize("hasAuthority('USER_DELETE')")
    @ApiMessage("Delete a user")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") Long id) {
        this.userService.handleDeleteUser(id);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    // superadmin(admin, host, user), admin(host, user)
    @GetMapping("/users")
    @PreAuthorize("hasAuthority('USER_LIST_ALL')")
    @ApiMessage("Fetch all users")
    public ResponseEntity<ResultPaginationDTO> getAllUser(
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String keyword,
            Pageable pageable
    ) {

        Specification<User> spec = Specification
                .where(UserSpecification.visibleByCurrentUser())
                .and(UserSpecification.hasRole(role))
                .and(UserSpecification.keyword(keyword));

        ResultPaginationDTO res = userService.handleListUser(spec, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    // superadmin
    @PutMapping("/users/{id}/roles")
    @PreAuthorize("hasAuthority('USER_ASSIGN_ROLE')")
    @ApiMessage("Assign users to roles")
    public ResponseEntity<ResAssignRoleDTO> assignRole(
            @PathVariable Long id,
            @Valid @RequestBody ReqAssignRoleDTO roleDTO
    ) {
        ResAssignRoleDTO res = this.userService.handleAssignRole(id, roleDTO);
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    // superadmin, admin
    @PutMapping("/users/{id}/status")
    @PreAuthorize("hasAuthority('USER_UPDATE_STATUS')")
    @ApiMessage("Update user status)")
    public ResponseEntity<ResUpdateUserStatusDTO> updateUserStatus(
            @PathVariable Long id,
            @Valid @RequestBody ReqUpdateUserStatusDTO req
    ) {
        ResUpdateUserStatusDTO res = this.userService.handleUpdateUserStatus(id, req.getStatus());
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

}
