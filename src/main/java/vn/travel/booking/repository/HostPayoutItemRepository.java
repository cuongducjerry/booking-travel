package vn.travel.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.travel.booking.entity.HostPayoutItem;

@Repository
public interface HostPayoutItemRepository extends JpaRepository<HostPayoutItem, Long> {

    boolean existsByBooking_Id(Long bookingId);

}
