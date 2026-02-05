package vn.travel.booking.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import vn.travel.booking.dto.request.user.*;
import vn.travel.booking.dto.response.user.*;
import vn.travel.booking.dto.response.*;
import vn.travel.booking.entity.Role;
import vn.travel.booking.entity.User;
import vn.travel.booking.mapper.PaginationMapper;
import vn.travel.booking.mapper.UserMapper;
import vn.travel.booking.repository.RoleRepository;
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
    private final RoleRepository roleRepository;

    public UserService(
            UserRepository userRepository,
            RoleService roleService,
            CloudinaryService cloudinaryService,
            UserMapper userMapper,
            PasswordEncoder passwordEncoder,
            PaginationMapper paginationMapper,
            RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleService = roleService;
        this.cloudinaryService = cloudinaryService;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.paginationMapper = paginationMapper;
        this.roleRepository = roleRepository;
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

        String currentRole = SecurityUtil.getCurrentUserRole(); // SUPER_ADMIN / ADMIN

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

        if (SecurityUtil.isAdmin()) {
            if (!List.of("USER", "HOST").contains(role.getName())) {
                throw new AccessDeniedException(
                        "ADMIN chỉ được tạo USER hoặc HOST"
                );
            }
        }

        if (!SecurityUtil.isAdmin() && !SecurityUtil.isSuperAdmin()) {
            throw new AccessDeniedException("Không có quyền tạo user");
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
    public void handleDeleteUser(Long id) {
        User currentUser = fetchUserById(id);
        this.userRepository.delete(currentUser);
    }

    @Transactional
    public ResUpdateProfileUserDTO handleUpdateProfileUser(ReqUpdateProfileUserDTO reqUser) {

        User currentUser = fetchUserById(reqUser.getId());

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

        User currentUser = this.userRepository.findByEmail(email);
        if(currentUser == null) {
            throw new UnauthenticatedException("User với email = " + email + " không tồn tại");
        }

        if(currentUser.getAvatarUrl() != null && currentUser.getAvatarUrl().length() > 0) {
            cloudinaryService.deleteAvatarUser(currentUser.getAvatarUrl());
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

        User currentUser = this.userRepository.findByEmail(email);
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

    @Transactional
    public ResAssignRoleDTO handleAssignRole(Long userId, ReqAssignRoleDTO roleDTO) {

        User user = fetchUserById(userId);

        Role role = this.roleRepository.findById(roleDTO.getRoleId())
                .orElseThrow(() -> new IdInvalidException(
                        "Role với id = " + roleDTO.getRoleId() + " không tồn tại"
                ));

        // =========================
        // CHECK PERMISSIONS
        // =========================
        String currentRole = SecurityUtil.getCurrentUserRole();

        if (currentRole == null) {
            throw new ForbiddenException("Unauthenticated");
        }

        // The admin cannot be assigned ADMIN or SUPER_ADMIN.
        if ("ADMIN".equals(currentRole)
                && ("ADMIN".equals(role.getName())
                || "SUPER_ADMIN".equals(role.getName()))) {
            throw new ForbiddenException(
                    "ADMIN không có quyền gán role ADMIN hoặc SUPER_ADMIN"
            );
        }

        // HOST / USER not assigned a role
        if ("HOST".equals(currentRole) || "USER".equals(currentRole)) {
            throw new ForbiddenException(
                    "Bạn không có quyền gán role"
            );
        }

        // =========================
        // Valid -> Assign Relay
        // =========================
        user.setRole(role);

        return this.userMapper.convertToResAssignRoleDTO(user, role);
    }

    @Transactional
    public ResUpdateUserStatusDTO handleUpdateUserStatus(Long userId, StatusUser reqStatusUser) {

        // The user is currently performing the action.
        String currentRole = SecurityUtil.getCurrentUserRole(); // ADMIN / SUPER_ADMIN

        User targetUser = fetchUserById(userId);
        String targetRole = targetUser.getRole().getName(); // USER / HOST / ADMIN / SUPER_ADMIN

        // If you are an ADMIN → you can only update USER and HOST information.
        if (SecurityUtil.isAdmin()) {
            if (!List.of(RoleCode.USER.toString(), RoleCode.HOST.toString())
                    .contains(targetRole)) {
                throw new AccessDeniedException(
                        "ADMIN không được cập nhật trạng thái của ADMIN hoặc SUPER_ADMIN"
                );
            }
        }

        // SUPER_ADMIN is perfectly fine.
        if (!SecurityUtil.isAdmin() && !SecurityUtil.isSuperAdmin()) {
            throw new AccessDeniedException("Không có quyền cập nhật trạng thái user");
        }

        targetUser.setStatus(reqStatusUser);

        return this.userMapper.convertToResUpdateUserStatusDTO(targetUser);
    }


    public User getUserByRefreshTokenAndEmail(String token, String email) {
        return this.userRepository.findByRefreshTokenAndEmail(token, email);
    }

    public ResUserDTO viewUserById(long userId) {

        User targetUser = fetchUserById(userId);

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
        return this.userRepository.findByEmail(username);
    }

    public User fetchUserById(Long id){
        return this.userRepository.findById(id)
                .orElseThrow(() ->
                        new IdInvalidException("User với id = " + id + " không tồn tại"));
    }

    public List<User> getAllAdmins() {
        return userRepository.findAllAdmins();
    }

    public boolean isEmailExist(String email){
        return this.userRepository.existsByEmail(email);
    }

    @Transactional
    public void updateUserToken(String token, String email) {
        User currentUser = this.handleGetUserByUsername(email);
        if(currentUser != null) {
            currentUser.setRefreshToken(token);
        }
    }

}
