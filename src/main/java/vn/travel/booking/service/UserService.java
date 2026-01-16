package vn.travel.booking.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import vn.travel.booking.dto.request.ReqCreateUserDTO;
import vn.travel.booking.dto.request.ReqUpdatePasswordDTO;
import vn.travel.booking.dto.request.ReqUpdateProfileUserDTO;
import vn.travel.booking.dto.response.*;
import vn.travel.booking.entity.Role;
import vn.travel.booking.entity.User;
import vn.travel.booking.mapper.UserMapper;
import vn.travel.booking.repository.UserRepository;
import vn.travel.booking.util.SecurityUtil;
import vn.travel.booking.util.constant.StatusUser;
import vn.travel.booking.util.error.IdInvalidException;
import vn.travel.booking.util.error.InvalidPasswordException;
import vn.travel.booking.util.error.UnauthenticatedException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    @Transactional
    public ResUserDTO handleRegisterUser(ReqCreateUserDTO reqCreateUserDTO) throws IdInvalidException {
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
        user.setPhone(reqCreateUserDTO.getPhone());
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

        return this.userMapper.convertToResUserDTO(user);

    }

    @Transactional
    public ResUserDTO handleCreateUser(ReqCreateUserDTO req) throws IdInvalidException {

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
        return this.userMapper.convertToResUserDTO(user);
    }

    @Transactional
    public void handleDeleteUser(long id) throws IdInvalidException {
        User currentUser = this.fetchUserById(id);
        if(currentUser == null) {
            throw new IdInvalidException("User với id = " + id + " không tồn tại");
        }
        this.userRepository.deleteById(id);
    }

    @Transactional
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

    @Transactional
    public ResUpdateAvatarUserDTO handleUpdateAvatar(MultipartFile file) throws UnauthenticatedException {

        String email = SecurityUtil.getCurrentUserLogin()
                .orElseThrow(() -> new UnauthenticatedException("Bạn chưa đăng nhập"));

        User currentUser = this.userRepository.findByEmailAndActiveTrue(email);
        if(currentUser == null) {
            throw new UnauthenticatedException("User với email = " + email + " không tồn tại");
        }

        // Upload Cloudinary
        String avatarUrl = cloudinaryService.uploadAvatar(file);

        // Update DB
        currentUser.setAvatarUrl(avatarUrl);
        this.userRepository.save(currentUser);

        return this.userMapper.convertToResUpdateAvatarUserDTO(avatarUrl, currentUser.getId());
    }

    @Transactional
    public ResUpdatePasswordDTO handleUpdatePassword(ReqUpdatePasswordDTO req) throws UnauthenticatedException, InvalidPasswordException {

        String email = SecurityUtil.getCurrentUserLogin()
                .orElseThrow(() -> new UnauthenticatedException("Bạn chưa đăng nhập"));

        User currentUser = this.userRepository.findByEmailAndActiveTrue(email);
        if(currentUser == null) {
            throw new UnauthenticatedException("User với email = " + email + " không tồn tại");
        }

        // 1. Check old password
        if (!passwordEncoder.matches(req.getOldPassword(), currentUser.getPassword())) {
            throw new InvalidPasswordException("Mật khẩu cũ không đúng");
        }

        // 2. Encode & update new password
        currentUser.setPassword(passwordEncoder.encode(req.getNewPassword()));
        userRepository.save(currentUser);

        return new ResUpdatePasswordDTO("Đổi mật khẩu thành công");
    }


    public ResUserDTO viewUserById(long userId) throws IdInvalidException {

        User targetUser = this.fetchUserById(userId);
        if(targetUser == null) {
            throw new IdInvalidException("User với id = " + userId + " không tồn tại");
        }

        return this.userMapper.convertToResUserDTO(targetUser);
    }

    public ResultPaginationDTO handleListUser(Specification spec, Pageable pageable) {
        Page<User> pageUser = this.userRepository.findAll(spec, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();

        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());

        mt.setPages(pageUser.getTotalPages());
        mt.setTotal(pageUser.getTotalElements());

        rs.setMeta(mt);

        List<ResUserDTO> listUser = pageUser.getContent().stream()
                .map(item -> this.userMapper.convertToResUserDTO(item))
                .collect(Collectors.toList());

        rs.setResult(listUser);
        return rs;

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

    @Transactional
    public void updateUserToken(String token, String email) {
        User currentUser = this.handleGetUserByUsername(email);
        if(currentUser != null) {
            currentUser.setRefreshToken(token);
            this.userRepository.save(currentUser);
        }
    }



}
