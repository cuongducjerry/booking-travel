package vn.travel.booking.specification;

import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import vn.travel.booking.entity.Property;
import vn.travel.booking.util.constant.PropertyStatus;

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
}
