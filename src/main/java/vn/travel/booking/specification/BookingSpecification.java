package vn.travel.booking.specification;

import org.springframework.data.jpa.domain.Specification;
import vn.travel.booking.entity.Booking;
import vn.travel.booking.util.constant.BookingStatus;

public class BookingSpecification {

    public static Specification<Booking> belongsToHost(Long hostId) {
        return (root, query, cb) ->
                cb.equal(
                        root.get("property")
                                .get("host")
                                .get("id"),
                        hostId
                );
    }

    public static Specification<Booking> hasStatus(BookingStatus status) {
        return (root, query, cb) ->
                status == null ? cb.conjunction()
                        : cb.equal(root.get("status"), status);
    }
}

