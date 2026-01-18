package vn.travel.booking.controller;

import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.travel.booking.dto.request.user.*;
import vn.travel.booking.dto.response.user.*;
import vn.travel.booking.dto.response.*;
import vn.travel.booking.entity.User;
import vn.travel.booking.service.UserService;
import vn.travel.booking.specification.UserSpecification;
import vn.travel.booking.util.annotation.ApiMessage;

@RestController
@RequestMapping("/api/v1")
public class UserController {

    private final UserService userService;

    public UserController(
            UserService userService) {
        this.userService = userService;
    }


    @PostMapping("/users")
    @PreAuthorize("hasAuthority('USER_CREATE')")
    @ApiMessage("Create a new user")
    public ResponseEntity<ResUserDTO> register(@Valid @RequestBody ReqCreateUserDTO reqUser) {
        ResUserDTO res = this.userService.handleCreateUser(reqUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

    @DeleteMapping("/users/{id}")
    @PreAuthorize("hasAuthority('USER_DELETE')")
    @ApiMessage("Delete a user")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") long id) {
        this.userService.handleDeleteUser(id);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    @PutMapping("/users/profile")
    @PreAuthorize("hasAuthority('USER_UPDATE_PROFILE')")
    @ApiMessage("Update a profile user")
    public ResponseEntity<ResUpdateProfileUserDTO> updateUser(@RequestBody ReqUpdateProfileUserDTO reqUser) {

        ResUpdateProfileUserDTO resUpdateProfileUserDTO = this.userService.handleUpdateProfileUser(reqUser);
        return ResponseEntity.status(HttpStatus.OK).body(resUpdateProfileUserDTO);
    }

    @PutMapping("/users/avatar")
    @PreAuthorize("hasAuthority('USER_UPDATE_AVATAR')")
    @ApiMessage("Update a avatar image user")
    public ResponseEntity<ResUpdateAvatarUserDTO> updateAvatar(
            @RequestParam MultipartFile file
    ) {
        ResUpdateAvatarUserDTO resUpdateAvatarUserDTO = userService.handleUpdateAvatar(file);
        return ResponseEntity.status(HttpStatus.OK).body(resUpdateAvatarUserDTO);
    }

    @PutMapping("/users/password")
    @PreAuthorize("hasAuthority('USER_UPDATE_PASSWORD')")
    @ApiMessage("Update password user")
    public ResponseEntity<ResUpdatePasswordDTO> updatePassword(
            @Valid @RequestBody ReqUpdatePasswordDTO req
    )  {

        return ResponseEntity.status(HttpStatus.OK).body(this.userService.handleUpdatePassword(req));
    }


    @GetMapping("/users/{id}")
    @PreAuthorize("hasAuthority('USER_VIEW') and @userSecurity.canViewUser(#id)")
    @ApiMessage("Fetch user by id")
    public ResponseEntity<ResUserDTO> getUserById(@PathVariable("id") long id) {
        return ResponseEntity.status(HttpStatus.OK).body(this.userService.viewUserById(id));
    }

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

    @PutMapping("/users/{id}/roles")
    @PreAuthorize("hasAuthority('USER_ASSIGN_ROLE')")
    @ApiMessage("Assign users to roles")
    public ResponseEntity<ResAssignRoleDTO> assignRole(
            @PathVariable Long id,
            @RequestBody ReqAssignRoleDTO roleDTO
    ) {
        ResAssignRoleDTO res = this.userService.handleAssignRole(id, roleDTO);
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    @PutMapping("/users/{id}/status")
    @PreAuthorize("hasAuthority('USER_UPDATE_STATUS')")
    @ApiMessage("Update user status)")
    public ResponseEntity<ResUpdateUserStatusDTO> updateUserStatus(
            @PathVariable long id,
            @Valid @RequestBody ReqUpdateUserStatusDTO req
    ) {
        ResUpdateUserStatusDTO res = this.userService.handleUpdateUserStatus(id, req.getStatus());
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

}
