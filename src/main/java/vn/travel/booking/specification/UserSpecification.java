package vn.travel.booking.specification;

import org.springframework.data.jpa.domain.Specification;
import vn.travel.booking.entity.User;

public class UserSpecification {

    public static Specification<User> hasRole(String role) {
        return (root, query, cb) -> {
            if (role == null) return null;
            return cb.equal(root.get("role").get("name"), role);
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

