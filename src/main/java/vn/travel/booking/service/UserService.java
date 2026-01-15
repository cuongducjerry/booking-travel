package vn.travel.booking.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import vn.travel.booking.domain.Role;
import vn.travel.booking.domain.User;
import vn.travel.booking.domain.response.ResCreateUserDTO;
import vn.travel.booking.domain.response.ResUpdateAvatarUserDTO;
import vn.travel.booking.domain.response.ResUpdateProfileUserDTO;
import vn.travel.booking.repository.UserRepository;
import vn.travel.booking.util.error.IdInvalidException;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleService roleService;
    private final CloudinaryService cloudinaryService;

    public UserService(
            UserRepository userRepository,
            RoleService roleService,
            CloudinaryService cloudinaryService) {
        this.userRepository = userRepository;
        this.roleService = roleService;
        this.cloudinaryService = cloudinaryService;
    }

    public User handleCreateUser(User user) {

        // check role
        if(user.getRole() != null) {
            Role r = this.roleService.fetchById(user.getRole().getId());
            user.setRole(r != null ? r : null);
        }

        return userRepository.save(user);
    }

    public void handleDeleteUser(long id){
        this.userRepository.deleteById(id);
    }

    public User handleUpdateProfileUser(User reqUser){
        User currentUser = this.fetchUserById(reqUser.getId());
        if(currentUser != null) {
            currentUser.setFullName(reqUser.getFullName());
            currentUser.setPhone(reqUser.getPhone());
            currentUser.setBio(reqUser.getBio());
            currentUser.setAddress(reqUser.getAddress());
            currentUser.setEmail(reqUser.getAge());

            // update
            currentUser = this.userRepository.save(currentUser);
        }
        return currentUser;
    }

    public String handleUpdateAvatar(User currentUser, MultipartFile file){

        // Upload Cloudinary
        String avatarUrl = cloudinaryService.uploadAvatar(file);

        // Update DB
        currentUser.setAvatarUrl(avatarUrl);
        this.userRepository.save(currentUser);

        return avatarUrl;
    }

    public User handleGetUserByUsername(String username){
        return this.userRepository.findByEmailAndActiveTrue(username);
    }

    public User fetchUserById(long id){
        Optional<User> userOptional = this.userRepository.findById(id);
        if(userOptional.isPresent()){
            return userOptional.get();
        }
        return null;
    }

    public boolean isEmailExist(String email){
        return this.userRepository.existsByEmailAndActiveTrue(email);
    }

    public void updateUserToken(String token, String email) {
        User currentUser = this.handleGetUserByUsername(email);
        if(currentUser != null) {
            currentUser.setRefreshToken(token);
            this.userRepository.save(currentUser);
        }
    }

    public ResCreateUserDTO convertToResCreateUserDTO(User user) {
        ResCreateUserDTO res = new ResCreateUserDTO();
        res.setId(user.getId());
        res.setEmail(user.getEmail());
        res.setFullName(user.getFullName());
        res.setPhone(user.getPhone());
        res.setAddress(user.getAddress());
        res.setAge(user.getAge());

        return res;
    }

    public ResUpdateProfileUserDTO convertToResUpdateProfileUserDTO(User user) {
        ResUpdateProfileUserDTO res = new ResUpdateProfileUserDTO();
        res.setId(user.getId());
        res.setFullName(user.getFullName());
        res.setPhone(user.getPhone());
        res.setAddress(user.getAddress());
        res.setAge(user.getAge());
        res.setBio(user.getBio());
        res.setUpdatedAt(user.getUpdatedAt());

        return res;
    }

    public ResUpdateAvatarUserDTO convertToResUpdateAvatarUserDTO(String avatarUrl, long userId) {
        ResUpdateAvatarUserDTO res = new ResUpdateAvatarUserDTO();
        res.setUrlImage(avatarUrl);
        res.setUserId(userId);
        return res;
    }

}
