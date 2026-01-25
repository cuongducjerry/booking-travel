package vn.travel.booking.specification;

import org.springframework.data.jpa.domain.Specification;
import vn.travel.booking.entity.Amenity;
import vn.travel.booking.entity.HostPayout;
import vn.travel.booking.util.constant.PayoutStatus;

public class PayoutSpecification {

    public static Specification<HostPayout> status(PayoutStatus status) {
        return (root, query, cb) -> {
            if (status == null) return null;

            return cb.equal(root.get("status"), status);
        };
    }

    public static Specification<HostPayout> byHost(Long hostId) {
        return (root, query, cb) ->
                cb.equal(root.get("host").get("id"), hostId);
    }
}

