package vn.travel.booking.specification;

import org.springframework.data.jpa.domain.Specification;
import vn.travel.booking.entity.HostContract;
import vn.travel.booking.util.constant.ContractStatus;

public class HostContractSpecification {

    public static Specification<HostContract> byHost(Long hostId) {
        return (root, query, cb) ->
                cb.equal(root.get("host").get("id"), hostId);
    }

    public static Specification<HostContract> hasContractCode(String code) {
        return (root, query, cb) -> {
            if (code == null || code.isBlank()) return null;
            return cb.like(
                    cb.lower(root.get("contractCode")),
                    "%" + code.toLowerCase() + "%"
            );
        };
    }

    public static Specification<HostContract> hasStatus(ContractStatus status) {
        return (root, query, cb) -> {
            if (status == null) return null;
            return cb.equal(root.get("status"), status);
        };
    }
}

