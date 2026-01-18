package vn.travel.booking.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import vn.travel.booking.dto.request.ReqLoginDTO;
import vn.travel.booking.dto.response.ResLoginDTO;
import vn.travel.booking.entity.Permission;
import vn.travel.booking.entity.User;
import vn.travel.booking.util.SecurityUtil;
import vn.travel.booking.util.error.IdInvalidException;
import java.util.List;

@Service
public class AuthService {

    @Autowired
    private AuthenticationManagerBuilder authenticationManagerBuilder;

    @Autowired
    private UserService userService;

    @Autowired
    private SecurityUtil securityUtil;

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

}