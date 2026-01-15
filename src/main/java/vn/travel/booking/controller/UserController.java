package vn.travel.booking.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.travel.booking.dto.request.ReqCreateUserDTO;
import vn.travel.booking.dto.request.ReqUpdateProfileUserDTO;
import vn.travel.booking.entity.User;
import vn.travel.booking.dto.response.ResCreateUserDTO;
import vn.travel.booking.dto.response.ResUpdateAvatarUserDTO;
import vn.travel.booking.dto.response.ResUpdateProfileUserDTO;
import vn.travel.booking.mapper.UserMapper;
import vn.travel.booking.service.UserService;
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
    public ResponseEntity<ResCreateUserDTO> register(@Valid @RequestBody ReqCreateUserDTO reqUser) throws IdInvalidException {
        ResCreateUserDTO res = this.userService.handleCreateUser(reqUser);
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

}
