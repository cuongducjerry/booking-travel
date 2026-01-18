package vn.travel.booking.specification;

import org.springframework.data.jpa.domain.Specification;
import vn.travel.booking.entity.Amenity;

public class AmenitySpecification {

    public static Specification<Amenity> keyword(String keyword) {
        return (root, query, cb) -> {
            if (keyword == null || keyword.isBlank()) return null;

            String pattern = "%" + keyword.toLowerCase() + "%";

            return cb.like(cb.lower(root.get("name")), pattern);
        };
    }

}
