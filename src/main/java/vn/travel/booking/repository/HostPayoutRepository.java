package vn.travel.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import vn.travel.booking.entity.HostContract;
import vn.travel.booking.entity.HostPayout;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface HostPayoutRepository extends JpaRepository<HostPayout, Long>, JpaSpecificationExecutor<HostPayout> {

    List<HostPayout> findByHost_Id(Long hostId);

    // PREVENT DOUBLE MONTHLY PAYOUTS
    boolean existsByContractAndPeriodFromAndPeriodTo(
            HostContract contract,
            LocalDate periodFrom,
            LocalDate periodTo
    );

    boolean existsByHost_IdAndContract_IdAndPeriodFromAndPeriodTo(
            Long hostId,
            Long contractId,
            LocalDate periodFrom,
            LocalDate periodTo
    );

}

