package vn.travel.booking.specification;

import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;
import vn.travel.booking.entity.Booking;
import vn.travel.booking.entity.Property;
import vn.travel.booking.entity.Role;
import vn.travel.booking.entity.User;
import vn.travel.booking.util.SecurityUtil;

public class UserSpecification {

    public static Specification<User> visibleByCurrentUser() {
        return (root, query, cb) -> {

            Long currentUserId = SecurityUtil.getCurrentUserId();
            Join<User, Role> roleJoin = root.join("role");

            // SUPER_ADMIN: see all
            if (SecurityUtil.isSuperAdmin()) {
                return cb.conjunction();
            }

            // ADMIN_* (Ops / Finance): only USER + HOST
            if (SecurityUtil.isAdmin()) {
                return roleJoin.get("name").in("USER", "HOST");
            }

            return cb.disjunction();
        };
    }

    public static Specification<User> hasRole(String role) {
        return (root, query, cb) -> {

            if (role == null || role.isBlank()) {
                return cb.conjunction();
            }

            // Only SUPER_ADMIN can filter ADMIN
            if ("ADMIN".equals(role) && !SecurityUtil.isSuperAdmin()) {
                return cb.disjunction();
            }

            return cb.equal(root.join("role").get("name"), role);
        };
    }


    public static Specification<User> keyword(String keyword) {
        return (root, query, cb) -> {
            if (keyword == null || keyword.isBlank()) return null;

            String pattern = "%" + keyword.toLowerCase() + "%";

            return cb.or(
                    cb.like(cb.lower(root.get("email")), pattern),
                    cb.like(cb.lower(root.get("fullName")), pattern)
            );
        };
    }
}

