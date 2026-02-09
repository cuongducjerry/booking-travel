package vn.travel.booking.config;

import com.nimbusds.jose.util.Base64;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;
import vn.travel.booking.entity.User;
import vn.travel.booking.repository.UserRepository;
import vn.travel.booking.util.SecurityUtil;
import vn.travel.booking.util.error.NameInvalidException;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.Principal;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class UserHandshakeHandler extends DefaultHandshakeHandler {

    @Value("${jwt.base64-secret}")
    private String jwtKey;

    @Override
    protected Principal determineUser(
            ServerHttpRequest request,
            WebSocketHandler wsHandler,
            Map<String, Object> attributes
    ) {

        if (!(request instanceof ServletServerHttpRequest servletRequest)) {
            return null;
        }

        String token =
                servletRequest.getServletRequest().getParameter("token");

        if (token == null) {
            System.out.println("[WS] Missing token");
            return null;
        }

        try {
            NimbusJwtDecoder decoder =
                    NimbusJwtDecoder.withSecretKey(getSecretKey())
                            .macAlgorithm(SecurityUtil.JWT_ALGORITHM)
                            .build();

            Jwt jwt = decoder.decode(token);

            Map<String, Object> userClaim = jwt.getClaim("user");
            Long userId =
                    ((Number) userClaim.get("id")).longValue();

            System.out.println("[WS] Bind userId = " + userId);

            return () -> userId.toString();

        } catch (Exception e) {
            System.out.println("[WS] Invalid JWT: " + e.getMessage());
            return null;
        }
    }

    private SecretKey getSecretKey() {
        byte[] keyBytes = Base64.from(jwtKey).decode();
        return new SecretKeySpec(
                keyBytes,
                0,
                keyBytes.length,
                SecurityUtil.JWT_ALGORITHM.getName()
        );
    }
}


