package vn.travel.booking.specification;

import org.springframework.data.jpa.domain.Specification;
import vn.travel.booking.entity.HostFee;
import vn.travel.booking.util.SecurityUtil;
import vn.travel.booking.util.constant.FeeStatus;

public class HostFeeSpecification {

    public static Specification<HostFee> visibleByCurrentHost() {
        return (root, query, cb) ->
                cb.equal(root.get("hostId"), SecurityUtil.getCurrentUserId());
    }

    public static Specification<HostFee> hasStatus(FeeStatus status) {
        return status == null
                ? null
                : (root, query, cb) -> cb.equal(root.get("status"), status);
    }
}



