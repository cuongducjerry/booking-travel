package vn.travel.booking.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.travel.booking.dto.request.ReqCreateUserDTO;
import vn.travel.booking.dto.request.ReqLoginDTO;
import vn.travel.booking.dto.response.ResUserDTO;
import vn.travel.booking.dto.response.ResLoginDTO;
import vn.travel.booking.service.AuthService;
import vn.travel.booking.service.UserService;
import vn.travel.booking.util.SecurityUtil;
import vn.travel.booking.util.annotation.ApiMessage;
import vn.travel.booking.util.error.IdInvalidException;

@RestController
@RequestMapping("/api/v1")
public class AuthController {

    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final SecurityUtil securityUtil;
    private final UserService userService;
    private final AuthService authService;

    public AuthController(
            AuthenticationManagerBuilder authenticationManagerBuilder,
            SecurityUtil securityUtil,
            UserService userService,
            AuthService authService) {
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.securityUtil = securityUtil;
        this.userService = userService;
        this.authService = authService;
    }

    @Value("${jwt.refresh-token-validity-in-seconds}")
    private long refreshTokenExpiration;

    @PostMapping("/auth/register")
    @ApiMessage("Register a new user")
    public ResponseEntity<ResUserDTO> register(@Valid @RequestBody ReqCreateUserDTO reqUser) throws IdInvalidException {
        ResUserDTO resUserDTO = this.userService.handleRegisterUser(reqUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(resUserDTO);
    }

    @PostMapping("/auth/login")
    public ResponseEntity<ResLoginDTO> login(@Valid @RequestBody ReqLoginDTO loginDto) {

        try {
            ResLoginDTO res = authService.handleLogin(loginDto);

            // set cookies
            ResponseCookie resCookies = ResponseCookie
                    .from("refresh_token", res.getRefreshToken())
                    .httpOnly(true)
                    .secure(true)
                    .path("/")
                    .maxAge(refreshTokenExpiration)
                    .build();

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, resCookies.toString())
                    .body(res);
        } catch (DisabledException ex) {
            throw new DisabledException(ex.getMessage());
        } catch (LockedException ex) {
            throw new LockedException(ex.getMessage());
        } catch (AuthenticationException ex) {
            throw new BadCredentialsException("Username hoặc password không hợp lệ");
        }
    }

    @PostMapping("/auth/logout")
    @ApiMessage("Logout User")
    public ResponseEntity<Void> logout() throws IdInvalidException {
        String email = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get() : "";

        if (email.equals("")) {
            throw new IdInvalidException("Access Token không hợp lệ");
        }

        // update refresh token = null
        this.userService.updateUserToken(null, email);

        // remove refresh token cookie
        ResponseCookie deleteSpringCookie = ResponseCookie
                .from("refresh_token", null)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, deleteSpringCookie.toString())
                .body(null);
    }

}
