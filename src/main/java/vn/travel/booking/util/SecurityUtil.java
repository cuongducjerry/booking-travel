package vn.travel.booking.util;

import com.nimbusds.jose.util.Base64;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;
import vn.travel.booking.entity.Permission;
import vn.travel.booking.entity.User;
import vn.travel.booking.dto.response.ResLoginDTO;
import vn.travel.booking.service.UserService;
import vn.travel.booking.util.error.ForbiddenException;
import vn.travel.booking.util.error.UnauthenticatedException;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class SecurityUtil {

    /**
     * Encoder used to generate JWT tokens.
     */
    private final JwtEncoder jwtEncoder;
    private final UserService userService;

    public SecurityUtil(
            JwtEncoder jwtEncoder,
            UserService userService) {
        this.jwtEncoder = jwtEncoder;
        this.userService = userService;
    }

    /**
     * HMAC algorithm used for signing JWT tokens.
     */
    public static final MacAlgorithm JWT_ALGORITHM = MacAlgorithm.HS512;

    /**
     * Base64-encoded secret key used for JWT signing and verification.
     */
    @Value("${jwt.base64-secret}")
    private String jwtKey;

    /**
     * Access token validity duration in seconds.
     */
    @Value("${jwt.access-token-validity-in-seconds}")
    private long accessTokenExpiration;

    /**
     * Refresh token validity duration in seconds.
     */
    @Value("${jwt.refresh-token-validity-in-seconds}")
    private long refreshTokenExpiration;


    public String createAccessToken(ResLoginDTO dto) {
        ResLoginDTO.UserInsideToken userToken = new ResLoginDTO.UserInsideToken();
        userToken.setId(dto.getUser().getId());
        userToken.setEmail(dto.getUser().getEmail());
        userToken.setFullName(dto.getUser().getFullName());

        Instant now = Instant.now();
        Instant validity = now.plus(this.accessTokenExpiration, ChronoUnit.SECONDS);

        List<String> listAuthority = dto.getPermissions();
        String role = dto.getUser().getRole(); // SUPER_ADMIN / ADMIN / HOST / USER

        // @formatter:off
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuedAt(now)
                .expiresAt(validity)
                .subject(dto.getUser().getEmail())
                .claim("user", userToken)
                .claim("permission", listAuthority)
                .claim("role", role)
                .build();

        JwsHeader jwsHeader = JwsHeader.with(JWT_ALGORITHM).build();
        return this.jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, claims)).getTokenValue();

    }


    public String createRefreshToken(ResLoginDTO dto) {

        Instant now = Instant.now();
        Instant validity = now.plus(this.refreshTokenExpiration, ChronoUnit.SECONDS);

        ResLoginDTO.UserInsideToken userToken = new ResLoginDTO.UserInsideToken();
        userToken.setId(dto.getUser().getId());
        userToken.setEmail(dto.getUser().getEmail());
        userToken.setFullName(dto.getUser().getFullName());

        // @formatter:off
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuedAt(now)
                .expiresAt(validity)
                .subject(dto.getUser().getEmail())
                .claim("user", userToken)
                .build();

        JwsHeader jwsHeader = JwsHeader.with(JWT_ALGORITHM).build();
        return this.jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, claims)).getTokenValue();

    }


    /**
     * Builds a {@link SecretKey} from the Base64-encoded JWT secret.
     *
     * @return SecretKey used for HMAC JWT signing and verification
     */
    private SecretKey getSecretKey() {
        byte[] keyBytes = Base64.from(jwtKey).decode();
        return new SecretKeySpec(keyBytes, 0, keyBytes.length, JWT_ALGORITHM.getName());
    }

    /**
     * Validates and decodes a refresh token.
     *
     * @param token the refresh JWT token
     * @return decoded {@link Jwt} if the token is valid
     * @throws Exception if the token is invalid or expired
     */
    public Jwt checkValidRefreshToken(String token) {
        NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withSecretKey(
                getSecretKey()).macAlgorithm(SecurityUtil.JWT_ALGORITHM).build();
        try {
            return jwtDecoder.decode(token);
        } catch (Exception e) {
            System.out.println(">>> Refresh Token error: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Retrieves the username of the currently authenticated user.
     *
     * @return Optional containing the username, or empty if not authenticated
     */
    public static Optional<String> getCurrentUserLogin() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        return Optional.ofNullable(extractPrincipal(securityContext.getAuthentication()));
    }

    public static String getCurrentUserRole() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return null;

        if (auth.getPrincipal() instanceof Jwt jwt) {
            return jwt.getClaimAsString("role");
        }
        return null;
    }

    public static Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return null;

        if (auth.getPrincipal() instanceof Jwt jwt) {

            Map<String, Object> userClaim = jwt.getClaim("user");
            if (userClaim == null) return null;

            Object id = userClaim.get("id");
            if (id instanceof Number number) {
                return number.longValue();
            }
        }
        return null;
    }

    /**
     * Extracts the principal (username/subject) from the authentication object.
     *
     * @param authentication current authentication
     * @return username, JWT subject, or null if not resolvable
     */
    private static String extractPrincipal(Authentication authentication) {
        if (authentication == null) {
            return null;
        } else if (authentication.getPrincipal() instanceof UserDetails springSecurityUser) {
            return springSecurityUser.getUsername();
        } else if (authentication.getPrincipal() instanceof Jwt jwt) {
            return jwt.getSubject();
        } else if (authentication.getPrincipal() instanceof String s) {
            return s;
        }
        return null;
    }

}
