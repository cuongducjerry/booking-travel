package vn.travel.booking.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import vn.travel.booking.dto.request.ReqCreateUserDTO;
import vn.travel.booking.dto.request.ReqUpdateProfileUserDTO;
import vn.travel.booking.entity.Role;
import vn.travel.booking.entity.User;
import vn.travel.booking.dto.response.ResCreateUserDTO;
import vn.travel.booking.dto.response.ResUpdateAvatarUserDTO;
import vn.travel.booking.dto.response.ResUpdateProfileUserDTO;
import vn.travel.booking.mapper.UserMapper;
import vn.travel.booking.repository.UserRepository;
import vn.travel.booking.util.constant.StatusUser;
import vn.travel.booking.util.error.IdInvalidException;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleService roleService;
    private final CloudinaryService cloudinaryService;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserService(
            UserRepository userRepository,
            RoleService roleService,
            CloudinaryService cloudinaryService,
            UserMapper userMapper,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleService = roleService;
        this.cloudinaryService = cloudinaryService;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public ResCreateUserDTO handleRegisterUser(ReqCreateUserDTO reqCreateUserDTO) throws IdInvalidException {
        boolean isEmailExist = this.isEmailExist(reqCreateUserDTO.getEmail());
        if(isEmailExist) {
            throw new IdInvalidException(
                    "Email " + reqCreateUserDTO.getEmail() + " đã tồn tại, vui lòng sư dụng email khác."
            );
        }

        // 2. check role
        Role role = roleService.fetchById(reqCreateUserDTO.getRole().getId());
        if (role == null) {
            throw new IdInvalidException("Role không tồn tại");
        }

        String hashPassword = this.passwordEncoder.encode(reqCreateUserDTO.getPassword());

        User user = new User();
        user.setEmail(reqCreateUserDTO.getEmail());
        user.setPassword(hashPassword);
        user.setActive(true);
        user.setFullName(reqCreateUserDTO.getFullName());
        user.setAddress(reqCreateUserDTO.getAddress());
        user.setAge(reqCreateUserDTO.getAge());

        if(role.getName().equals("USER")) {
            user.setStatus(StatusUser.APPROVED);
        } else if(role.getName().equals("HOST")) {
            user.setStatus(StatusUser.PENDING);
        }

        user.setRole(role);

        this.userRepository.save(user);

        return this.userMapper.convertToResCreateUserDTO(user);

    }

    public ResCreateUserDTO handleCreateUser(ReqCreateUserDTO req) throws IdInvalidException {

        // 1. check email
        if (this.isEmailExist(req.getEmail())) {
            throw new IdInvalidException(
                    "Email " + req.getEmail() + " đã tồn tại"
            );
        }

        // 2. check role
        Role role = roleService.fetchById(req.getRole().getId());
        if (role == null) {
            throw new IdInvalidException("Role không tồn tại");
        }

        // 3. map Req → Entity
        User user = new User();
        user.setEmail(req.getEmail());
        user.setFullName(req.getFullName());
        user.setPhone(req.getPhone());
        user.setAddress(req.getAddress());
        user.setAge(req.getAge());
        user.setActive(true);

        // 4. business default
        user.setStatus(StatusUser.APPROVED);
        user.setRole(role);

        // 5. encode password
        user.setPassword(passwordEncoder.encode(req.getPassword()));

        // 6. save
        this.userRepository.save(user);

        // 7. map Entity -> Response
        return this.userMapper.convertToResCreateUserDTO(user);
    }

    public void handleDeleteUser(long id) throws IdInvalidException {
        User currentUser = this.fetchUserById(id);
        if(currentUser == null) {
            throw new IdInvalidException("User với id = " + id + " không tồn tại");
        }
        this.userRepository.deleteById(id);
    }

    public ResUpdateProfileUserDTO handleUpdateProfileUser(ReqUpdateProfileUserDTO reqUser) throws IdInvalidException {

        User currentUser = this.fetchUserById(reqUser.getId());
        if(currentUser == null) {
            throw new IdInvalidException("User với id = " + reqUser.getId() + " không tồn tại");
        }

        currentUser.setFullName(reqUser.getFullName());
        currentUser.setPhone(reqUser.getPhone());
        currentUser.setBio(reqUser.getBio());
        currentUser.setAddress(reqUser.getAddress());
        currentUser.setAge(reqUser.getAge());

        // update
        currentUser = this.userRepository.save(currentUser);

        return this.userMapper.convertToResUpdateProfileUserDTO(currentUser);
    }

    public ResUpdateAvatarUserDTO handleUpdateAvatar(Long userId, MultipartFile file) throws IdInvalidException {

        User currentUser = this.fetchUserById(userId);
        if(currentUser == null) {
            throw new IdInvalidException("User với id = " + userId + " không tồn tại");
        }

        // Upload Cloudinary
        String avatarUrl = cloudinaryService.uploadAvatar(file);

        // Update DB
        currentUser.setAvatarUrl(avatarUrl);
        this.userRepository.save(currentUser);

        return this.userMapper.convertToResUpdateAvatarUserDTO(avatarUrl, userId);
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



}
