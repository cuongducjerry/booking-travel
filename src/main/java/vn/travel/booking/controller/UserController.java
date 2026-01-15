package vn.travel.booking.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.travel.booking.domain.User;
import vn.travel.booking.domain.response.ResCreateUserDTO;
import vn.travel.booking.domain.response.ResUpdateAvatarUserDTO;
import vn.travel.booking.domain.response.ResUpdateProfileUserDTO;
import vn.travel.booking.service.UserService;
import vn.travel.booking.util.annotation.ApiMessage;
import vn.travel.booking.util.error.IdInvalidException;

@RestController
@RequestMapping("/api/v1")
public class UserController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public UserController(
            UserService userService,
            PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }


    @PostMapping("/users")
    @PreAuthorize("hasAuthority('USER_CREATE')")
    @ApiMessage("Create a new user")
    public ResponseEntity<ResCreateUserDTO> register(@Valid @RequestBody User postManUser) throws IdInvalidException {
        boolean isEmailExist = this.userService.isEmailExist(postManUser.getEmail());
        if(isEmailExist) {
            throw new IdInvalidException(
                    "Email " + postManUser.getEmail() + " đã tồn tại, vui lòng sư dụng email khác."
            );
        }

        String hashPassword = this.passwordEncoder.encode(postManUser.getPassword());
        postManUser.setPassword(hashPassword);
        User user = this.userService.handleCreateUser(postManUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(this.userService.convertToResCreateUserDTO(user));
    }

    @DeleteMapping("/users/{id}")
    @PreAuthorize("hasAuthority('USER_DELETE')")
    @ApiMessage("Delete a user")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") long id) throws IdInvalidException {
        User currentUser = this.userService.fetchUserById(id);
        if(currentUser == null) {
            throw new IdInvalidException("User với id = " + id + " không tồn tại");
        }
        this.userService.handleDeleteUser(id);
        return ResponseEntity.ok(null);
    }

    @PutMapping("/users/profile")
    @PreAuthorize("hasAuthority('USER_UPDATE_PROFILE')")
    @ApiMessage("Update a profile user")
    public ResponseEntity<ResUpdateProfileUserDTO> updateUser(@RequestBody User user) throws IdInvalidException{
        User currentUser = this.userService.handleUpdateProfileUser(user);
        if(currentUser == null) {
            throw new IdInvalidException("User với id = " + user.getId() + " không tồn tại");
        }
        return ResponseEntity.ok(this.userService.convertToResUpdateProfileUserDTO(currentUser));
    }

    @PutMapping("/users/avatar")
    @PreAuthorize("hasAuthority('USER_UPDATE_AVATAR')")
    @ApiMessage("Update a avatar image user")
    public ResponseEntity<ResUpdateAvatarUserDTO> updateAvatar(
            @RequestParam Long userId,
            @RequestParam MultipartFile file
    ) throws IdInvalidException {
        User currentUser = this.userService.fetchUserById(userId);
        if(currentUser == null) {
            throw new IdInvalidException("User với id = " + userId + " không tồn tại");
        }
        String avatarUrl = userService.handleUpdateAvatar(currentUser, file);
        return ResponseEntity.ok(this.userService.convertToResUpdateAvatarUserDTO(avatarUrl, userId));
    }

}
