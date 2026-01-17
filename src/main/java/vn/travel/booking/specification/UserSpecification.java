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

            String role = SecurityUtil.getCurrentUserRole();
            Long currentUserId = SecurityUtil.getCurrentUserId();

            if (role == null) {
                return cb.disjunction();
            }

            return switch (role) {

                case "SUPER_ADMIN", "ADMIN" ->
                        cb.conjunction(); // all

                case "HOST" -> {
                    // join User -> Booking -> Property
                    Join<User, Booking> bookingJoin = root.join("bookings");
                    Join<Booking, Property> propertyJoin = bookingJoin.join("property");

                    yield cb.equal(
                            propertyJoin.get("host").get("id"),
                            currentUserId
                    );
                }

                case "USER" ->
                        cb.equal(root.get("id"), currentUserId);

                default ->
                        cb.disjunction();
            };
        };
    }


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

