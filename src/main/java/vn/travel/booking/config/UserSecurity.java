package vn.travel.booking.config;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component("userSecurity")
public class UserSecurity {

    public boolean canViewUser(long targetUserId) {

        Authentication auth =
                SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            return false;
        }

        // ===== JWT MODE =====
        if (auth.getPrincipal() instanceof Jwt jwt) {

            List<String> permissions =
                    jwt.getClaimAsStringList("permission");

            if (permissions != null && permissions.contains("USER_VIEW_ALL")) {
                return true;
            }

            Map<String, Object> userClaim =
                    jwt.getClaim("user");

            if (userClaim == null) return false;

            Long currentUserId = ((Number) userClaim.get("id")).longValue();

            if (!currentUserId.equals(targetUserId)) {
                throw new AccessDeniedException(
                        "Bạn không có quyền xem người dùng này"
                );
            }
            return currentUserId.equals(targetUserId);
        }
        return false;
    }
}
