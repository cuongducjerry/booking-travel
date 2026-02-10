package vn.travel.booking.service;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.travel.booking.dto.request.ReqLoginDTO;
import vn.travel.booking.dto.request.SocialLoginReq;
import vn.travel.booking.dto.response.ResLoginDTO;
import vn.travel.booking.entity.Permission;
import vn.travel.booking.entity.User;
import vn.travel.booking.repository.RoleRepository;
import vn.travel.booking.repository.UserRepository;
import vn.travel.booking.service.notification.EmailService;
import vn.travel.booking.util.SecurityUtil;
import vn.travel.booking.util.constant.StatusUser;
import vn.travel.booking.util.error.BusinessException;
import vn.travel.booking.util.error.IdInvalidException;
import java.util.List;

@Service
public class AuthService {

    @Autowired
    private AuthenticationManagerBuilder authenticationManagerBuilder;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private SecurityUtil securityUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    public ResLoginDTO handleLogin(ReqLoginDTO loginDto) {

        // Enter username and password into Security.
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                loginDto.getUsername(), loginDto.getPassword());

        // user authentication => a function loadUserByUsername is needed.
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        // set the login information of the user into the context
        SecurityContextHolder.getContext().setAuthentication(authentication);

        ResLoginDTO res = new ResLoginDTO();
        User currentUserDB = this.userService.handleGetUserByUsername(loginDto.getUsername());

        if (currentUserDB != null) {
            ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin(
                    currentUserDB.getId(),
                    currentUserDB.getEmail(),
                    currentUserDB.getFullName(),
                    currentUserDB.getAvatarUrl(),
                    currentUserDB.getRole().getName());
            res.setUser(userLogin);
        }

        List<String> permissions = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        res.setPermissions(permissions);

        // create access token
        String accessToken = this.securityUtil.createAccessToken(res);
        res.setAccessToken(accessToken);

        // create refresh token
        String refreshToken = this.securityUtil.createRefreshToken(res);

        // update user
        this.userService.updateUserToken(refreshToken, loginDto.getUsername());

        res.setRefreshToken(refreshToken);

        return res;
    }

    public ResLoginDTO refresh(String refreshToken) {
        // check valid
        Jwt decodedToken = this.securityUtil.checkValidRefreshToken(refreshToken);
        String email = decodedToken.getSubject();

        // check user by token + email
        User currentUser = this.userService.getUserByRefreshTokenAndEmail(refreshToken, email);
        if (currentUser == null) {
            throw new IdInvalidException("Refresh Token không hợp lệ");
        }

        ResLoginDTO res = new ResLoginDTO();
        if(currentUser != null) {
            ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin(
                    currentUser.getId(),
                    currentUser.getEmail(),
                    currentUser.getFullName(),
                    currentUser.getAvatarUrl(),
                    currentUser.getRole().getName());
            res.setUser(userLogin);
        }

        List<String> permissions = currentUser.getRole()
                .getPermissions()
                .stream()
                .map(Permission::getCode)
                .toList();

        res.setPermissions(permissions);

        // create access token
        String newAccessToken = this.securityUtil.createAccessToken(res);
        res.setAccessToken(newAccessToken);

        // create refresh token
        String newRefreshToken = this.securityUtil.createRefreshToken(res);

        // update user
        this.userService.updateUserToken(refreshToken, currentUser.getEmail());

        res.setRefreshToken(refreshToken);

        return res;

    }

    public ResLoginDTO.UserGetAccount getUserAccount() {
        String email = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get() : "";

        User currentUserDB = this.userService.handleGetUserByUsername(email);
        ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin();
        ResLoginDTO.UserGetAccount userGetAccount = new ResLoginDTO.UserGetAccount();

        if (currentUserDB != null) {
            userLogin.setId(currentUserDB.getId());
            userLogin.setEmail(currentUserDB.getEmail());
            userLogin.setFullName(currentUserDB.getFullName());
            userLogin.setRole(currentUserDB.getRole().getName());
            userLogin.setAvatarUrl(currentUserDB.getAvatarUrl());
            userGetAccount.setUser(userLogin);
        }

        return userGetAccount;
    }

    @Transactional
    public ResLoginDTO socialLogin(SocialLoginReq req) {

        // 1. Find or create user
        User user = userRepository.findByEmail(req.getEmail());
        if (user == null) {
            user = createGoogleUser(req);
        }

        // 2. Build ResLoginDTO (same as handleLogin)
        ResLoginDTO res = new ResLoginDTO();

        ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin(
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                user.getAvatarUrl(),
                user.getRole().getName()
        );
        res.setUser(userLogin);

        List<String> permissions = user.getRole()
                .getPermissions()
                .stream()
                .map(Permission::getCode)
                .toList();
        res.setPermissions(permissions);

        // 4. Create access token
        String accessToken = securityUtil.createAccessToken(res);
        res.setAccessToken(accessToken);

        // 5. Create refresh token
        String refreshToken = securityUtil.createRefreshToken(res);
        res.setRefreshToken(refreshToken);

        // 6. Update refresh token vào DB
        userService.updateUserToken(refreshToken, user.getEmail());

        return res;
    }

    @Transactional
    public void forgotPassword(String email) {

        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new BusinessException("Email không tồn tại");
        }

        // 1. Generate random password (6 chars)
        String newPassword = RandomStringUtils.randomAlphanumeric(6);

        // 2. Encode password
        String encoded = passwordEncoder.encode(newPassword);
        user.setPassword(encoded);

        // 3. Save
        userRepository.save(user);

        // 4. Send email
        emailService.send(
                user.getEmail(),
                "Mật khẩu mới của bạn",
                "Mật khẩu mới của bạn là: " + newPassword
        );
    }

    private User createGoogleUser(SocialLoginReq req) {
        User user = User.builder()
                .email(req.getEmail())
                .fullName(req.getName())
                .avatarUrl(req.getAvatar())
                .password("")
                .role(roleRepository.findByName("USER"))
                .status(StatusUser.APPROVED)
                .active(true)
                .build();

        return userRepository.save(user);
    }

}