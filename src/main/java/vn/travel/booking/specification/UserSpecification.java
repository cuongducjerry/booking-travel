package vn.travel.booking.specification;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import vn.travel.booking.entity.Booking;
import vn.travel.booking.entity.Property;
import vn.travel.booking.entity.Role;
import vn.travel.booking.entity.User;
import vn.travel.booking.util.SecurityUtil;
import vn.travel.booking.util.constant.StatusUser;

import java.util.ArrayList;
import java.util.List;

public class UserSpecification {

    public static Specification<User> visibleByCurrentUser() {
        return (root, query, cb) -> {

            Join<User, Role> roleJoin = root.join("role");

            if (SecurityUtil.isSuperAdmin()) {
                return cb.conjunction();
            }

            if (SecurityUtil.isAdmin()) {
                return roleJoin.get("name").in("USER", "HOST");
            }

            return cb.disjunction();
        };
    }

    /** Filter role */
    public static Specification<User> hasRole(String role) {
        return (root, query, cb) -> {

            if (role == null || role.isBlank()) {
                return cb.conjunction();
            }

            // chỉ SUPER_ADMIN được filter ADMIN
            if ("ADMIN".equals(role) && !SecurityUtil.isSuperAdmin()) {
                return cb.disjunction();
            }

            return cb.equal(root.join("role").get("name"), role);
        };
    }

    /** Filter status */
    public static Specification<User> hasStatus(StatusUser status) {
        return (root, query, cb) -> {
            if (status == null) {
                return cb.conjunction();
            }
            return cb.equal(root.get("status"), status);
        };
    }

    /** Search keyword: fullName + role.name */
    public static Specification<User> keyword(String keyword) {
        return (root, query, cb) -> {

            if (keyword == null || keyword.isBlank()) {
                return cb.conjunction();
            }

            String pattern = "%" + keyword.toLowerCase() + "%";

            Join<User, Role> roleJoin = root.join("role");

            List<Predicate> predicates = new ArrayList<>();

            predicates.add(
                    cb.like(cb.lower(root.get("fullName")), pattern)
            );

            predicates.add(
                    cb.like(cb.lower(roleJoin.get("name")), pattern)
            );

            return cb.or(predicates.toArray(new Predicate[0]));
        };
    }
}
