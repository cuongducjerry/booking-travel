package vn.travel.booking.service;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import vn.travel.booking.dto.request.ReqLoginDTO;
import vn.travel.booking.dto.response.ResLoginDTO;
import vn.travel.booking.entity.User;
import vn.travel.booking.util.SecurityUtil;

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
}