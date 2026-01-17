package vn.travel.booking.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import vn.travel.booking.dto.request.user.ReqCreateUserDTO;
import vn.travel.booking.dto.request.user.ReqUpdatePasswordDTO;
import vn.travel.booking.dto.request.user.ReqUpdateProfileUserDTO;
import vn.travel.booking.dto.response.*;
import vn.travel.booking.dto.response.user.ResUpdateAvatarUserDTO;
import vn.travel.booking.dto.response.user.ResUpdatePasswordDTO;
import vn.travel.booking.dto.response.user.ResUpdateProfileUserDTO;
import vn.travel.booking.dto.response.user.ResUserDTO;
import vn.travel.booking.entity.Role;
import vn.travel.booking.entity.User;
import vn.travel.booking.mapper.PaginationMapper;
import vn.travel.booking.mapper.UserMapper;
import vn.travel.booking.repository.UserRepository;
import vn.travel.booking.util.SecurityUtil;
import vn.travel.booking.util.constant.RoleCode;
import vn.travel.booking.util.constant.StatusUser;
import vn.travel.booking.util.error.ForbiddenException;
import vn.travel.booking.util.error.IdInvalidException;
import vn.travel.booking.util.error.InvalidPasswordException;
import vn.travel.booking.util.error.UnauthenticatedException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleService roleService;
    private final CloudinaryService cloudinaryService;
    private final UserMapper userMapper;
    private final PaginationMapper paginationMapper;
    private final PasswordEncoder passwordEncoder;

    public UserService(
            UserRepository userRepository,
            RoleService roleService,
            CloudinaryService cloudinaryService,
            UserMapper userMapper,
            PasswordEncoder passwordEncoder,
            PaginationMapper paginationMapper) {
        this.userRepository = userRepository;
        this.roleService = roleService;
        this.cloudinaryService = cloudinaryService;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.paginationMapper = paginationMapper;
    }

    @Transactional
    public ResUserDTO handleRegisterUser(ReqCreateUserDTO reqCreateUserDTO) {
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

        if(role.getName().equals(RoleCode.USER.toString())) {
            user.setStatus(StatusUser.APPROVED);
        } else if(role.getName().equals(RoleCode.HOST.toString())) {
            user.setStatus(StatusUser.PENDING);
        }

        user.setRole(role);

        this.userRepository.save(user);

        return this.userMapper.convertToResUserDTO(user);

    }

    @Transactional
    public ResUserDTO handleCreateUser(ReqCreateUserDTO req) {

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
    public void handleDeleteUser(long id) {
        User currentUser = this.fetchUserById(id);
        if(currentUser == null) {
            throw new IdInvalidException("User với id = " + id + " không tồn tại");
        }
        this.userRepository.delete(currentUser);
    }

    @Transactional
    public ResUpdateProfileUserDTO handleUpdateProfileUser(ReqUpdateProfileUserDTO reqUser) {

        User currentUser = this.fetchUserById(reqUser.getId());
        if(currentUser == null) {
            throw new IdInvalidException("User với id = " + reqUser.getId() + " không tồn tại");
        }

        currentUser.setFullName(reqUser.getFullName());
        currentUser.setPhone(reqUser.getPhone());
        currentUser.setBio(reqUser.getBio());
        currentUser.setAddress(reqUser.getAddress());
        currentUser.setAge(reqUser.getAge());

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

        return this.userMapper.convertToResUpdateAvatarUserDTO(currentUser);
    }

    @Transactional
    public ResUpdatePasswordDTO handleUpdatePassword(ReqUpdatePasswordDTO req)  {

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

        return new ResUpdatePasswordDTO("Đổi mật khẩu thành công", currentUser.getUpdatedAt());
    }


    public ResUserDTO viewUserById(long userId) {

        User targetUser = this.fetchUserById(userId);
        if(targetUser == null) {
            throw new IdInvalidException("User với id = " + userId + " không tồn tại");
        }

        return this.userMapper.convertToResUserDTO(targetUser);
    }

    public ResultPaginationDTO handleListUser(Specification spec, Pageable pageable) {
        Page<User> pageUser = this.userRepository.findAll(spec, pageable);
        int pageNumber = pageable.getPageNumber() + 1;
        int pageSize = pageable.getPageSize();
        int totalPages = pageUser.getTotalPages();
        long totalElements = pageUser.getTotalElements();

        List<ResUserDTO> listUser = pageUser.getContent().stream()
                .map(item -> this.userMapper.convertToResUserDTO(item))
                .collect(Collectors.toList());

        ResultPaginationDTO res = this.paginationMapper.convertToResultPaginationDTO(pageNumber, pageSize, totalPages, totalElements, listUser);

        return res;

    }

    public User handleGetUserByUsername(String username){
        return this.userRepository.findByEmailAndActiveTrue(username);
    }

    public User fetchUserById(long id){
        return userRepository.findById(id)
                .orElseThrow(() ->
                        new IdInvalidException("User với id = " + id + " không tồn tại"));
    }

    public boolean isEmailExist(String email){
        return this.userRepository.existsByEmailAndActiveTrue(email);
    }

    @Transactional
    public void updateUserToken(String token, String email) {
        User currentUser = this.handleGetUserByUsername(email);
        if(currentUser != null) {
            currentUser.setRefreshToken(token);
        }
    }

}
