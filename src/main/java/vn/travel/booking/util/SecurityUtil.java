package vn.travel.booking.util;

import com.nimbusds.jose.util.Base64;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Optional;

@Service
public class SecurityUtil {

    /**
     * Encoder used to generate JWT tokens.
     */
    private final JwtEncoder jwtEncoder;

    public SecurityUtil(JwtEncoder jwtEncoder) {
        this.jwtEncoder = jwtEncoder;
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
