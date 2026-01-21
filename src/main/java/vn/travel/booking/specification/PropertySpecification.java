package vn.travel.booking.specification;

import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import vn.travel.booking.entity.Booking;
import vn.travel.booking.entity.Property;
import vn.travel.booking.entity.PropertyType;
import vn.travel.booking.entity.User;
import vn.travel.booking.util.SecurityUtil;
import vn.travel.booking.util.constant.PropertyStatus;

import java.time.LocalDate;

public class PropertySpecification {

    // title LIKE %title%
    public static Specification<Property> hasTitle(String title) {
        return (root, query, cb) -> {
            if (title == null || title.trim().isEmpty()) {
                return cb.conjunction();
            }
            return cb.like(
                    cb.lower(root.get("title")),
                    "%" + title.toLowerCase() + "%"
            );
        };
    }

    // status = ENUM
    public static Specification<Property> hasStatus(String status) {
        return (root, query, cb) -> {
            if (status == null || status.trim().isEmpty()) {
                return cb.conjunction();
            }
            return cb.equal(
                    root.get("status"),
                    PropertyStatus.valueOf(status.toUpperCase())
            );
        };
    }

    // propertyType.name = ?
    public static Specification<Property> hasPropertyType(String propertyType) {
        return (root, query, cb) -> {
            if (propertyType == null || propertyType.trim().isEmpty()) {
                return cb.conjunction();
            }
            Join<Object, Object> join = root.join("propertyType", JoinType.INNER);
            return cb.equal(
                    cb.lower(join.get("name")),
                    propertyType.toLowerCase()
            );
        };
    }

    public static Specification<Property> filterByCurrentUserRole() {
        return (root, query, cb) -> {

            // ADMIN / SUPER_ADMIN → see all
            if (SecurityUtil.isAdmin() || SecurityUtil.isSuperAdmin()) {
                return cb.conjunction();
            }

            // HOST → only view your own property
            if (SecurityUtil.isHost()) {
                Long userId = SecurityUtil.getCurrentUserId();
                if (userId == null) {
                    return cb.disjunction();
                }

                Join<Property, User> hostJoin = root.join("host", JoinType.INNER);
                return cb.equal(hostJoin.get("id"), userId);
            }

            // Other roles → cannot be viewed
            return cb.disjunction();
        };
    }

    // ===================================================================================================

    // status = PUBLISHED & active = true
    public static Specification<Property> isPublicProperty() {
        return (root, query, cb) ->
                cb.and(
                        cb.equal(root.get("status"), PropertyStatus.APPROVED),
                        cb.isTrue(root.get("active"))
                );
    }

    public static Specification<Property> hasGuestCapacity(Integer guests) {
        return (root, query, cb) -> {
            if (guests == null) return cb.conjunction();
            return cb.greaterThanOrEqualTo(root.get("maxGuests"), guests);
        };
    }

    public static Specification<Property> availableBetween(
            LocalDate checkIn,
            LocalDate checkOut
    ) {
        return (root, query, cb) -> {

            if (checkIn == null || checkOut == null) {
                return cb.conjunction();
            }

            // Subquery to find duplicate bookings
            Subquery<Long> sub = query.subquery(Long.class);
            Root<Booking> booking = sub.from(Booking.class);

            sub.select(booking.get("property").get("id"))
                    .where(
                            cb.and(
                                    cb.equal(booking.get("property"), root),
                                    cb.lessThan(booking.get("checkIn"), checkOut),
                                    cb.greaterThan(booking.get("checkOut"), checkIn)
                            )
                    );

            // DO NOT EXIST DUPLICATE BOOKINGS
            return cb.not(cb.exists(sub));
        };
    }

    public static Specification<Property> hasAddress(String address) {
        return (root, query, cb) -> {
            if (address == null || address.isBlank()) {
                return cb.conjunction();
            }
            return cb.like(
                    cb.lower(root.get("address")),
                    "%" + address.toLowerCase() + "%"
            );

        };
    }

    public static Specification<Property> hasPropertyTypeHome(String typeName) {
        return (root, query, cb) -> {
            if (typeName == null || typeName.isBlank()) {
                return cb.conjunction();
            }

            Join<Property, PropertyType> join =
                    root.join("propertyType", JoinType.INNER);

            return cb.equal(
                    cb.lower(join.get("name")),
                    typeName.toLowerCase()
            );
        };
    }


}
