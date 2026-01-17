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
                return cb.disjunction(); // không thấy gì
            }

            Join<User, Role> roleJoin = root.join("role");

            return switch (role) {

                case "SUPER_ADMIN" ->
                        cb.conjunction(); // see all

                case "ADMIN" ->
                        roleJoin.get("name").in("HOST", "USER");

                case "HOST" -> {
                    // Only the USER + must be related to this host's property.
                    Join<User, Booking> bookingJoin = root.join("bookings");
                    Join<Booking, Property> propertyJoin = bookingJoin.join("property");

                    yield cb.and(
                            cb.equal(roleJoin.get("name"), "USER"),
                            cb.equal(
                                    propertyJoin.get("host").get("id"),
                                    currentUserId
                            )
                    );
                }

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

