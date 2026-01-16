package vn.travel.booking.controller;

import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.travel.booking.dto.request.ReqCreateUserDTO;
import vn.travel.booking.dto.request.ReqUpdateProfileUserDTO;
import vn.travel.booking.dto.response.ResUserDTO;
import vn.travel.booking.dto.response.ResUpdateAvatarUserDTO;
import vn.travel.booking.dto.response.ResUpdateProfileUserDTO;
import vn.travel.booking.dto.response.ResultPaginationDTO;
import vn.travel.booking.entity.User;
import vn.travel.booking.service.UserService;
import vn.travel.booking.specification.UserSpecification;
import vn.travel.booking.util.annotation.ApiMessage;
import vn.travel.booking.util.error.IdInvalidException;

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
    public ResponseEntity<ResUserDTO> register(@Valid @RequestBody ReqCreateUserDTO reqUser) throws IdInvalidException {
        ResUserDTO res = this.userService.handleCreateUser(reqUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

    @DeleteMapping("/users/{id}")
    @PreAuthorize("hasAuthority('USER_DELETE')")
    @ApiMessage("Delete a user")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") long id) throws IdInvalidException {
        this.userService.handleDeleteUser(id);
        return ResponseEntity.ok(null);
    }

    @PutMapping("/users/profile")
    @PreAuthorize("hasAuthority('USER_UPDATE_PROFILE')")
    @ApiMessage("Update a profile user")
    public ResponseEntity<ResUpdateProfileUserDTO> updateUser(@RequestBody ReqUpdateProfileUserDTO reqUser) throws IdInvalidException{

        ResUpdateProfileUserDTO resUpdateProfileUserDTO = this.userService.handleUpdateProfileUser(reqUser);
        return ResponseEntity.ok(resUpdateProfileUserDTO);
    }

    @PutMapping("/users/avatar")
    @PreAuthorize("hasAuthority('USER_UPDATE_AVATAR')")
    @ApiMessage("Update a avatar image user")
    public ResponseEntity<ResUpdateAvatarUserDTO> updateAvatar(
            @RequestParam Long userId,
            @RequestParam MultipartFile file
    ) throws IdInvalidException {
        ResUpdateAvatarUserDTO resUpdateAvatarUserDTO = userService.handleUpdateAvatar(userId, file);
        return ResponseEntity.ok(resUpdateAvatarUserDTO);
    }

    @GetMapping("/users/{id}")
    @PreAuthorize("hasAuthority('USER_VIEW')")
    @ApiMessage("Fetch user by id")
    public ResponseEntity<ResUserDTO> getUserById(@PathVariable("id") long id) throws IdInvalidException {
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
                .where(UserSpecification.hasRole(role))
                .and(UserSpecification.keyword(keyword));

        ResultPaginationDTO res = userService.handleListUser(spec, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

}
