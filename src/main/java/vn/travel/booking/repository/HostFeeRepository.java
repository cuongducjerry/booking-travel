package vn.travel.booking.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import vn.travel.booking.entity.HostFee;

import java.util.Optional;

@Repository
public interface HostFeeRepository extends JpaRepository<HostFee, Long>, JpaSpecificationExecutor<HostFee> {

    boolean existsByBookingId(Long bookingId);

    Optional<HostFee> findByIdAndHostId(Long id, Long hostId);
}
