package vn.travel.booking.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.travel.booking.entity.HostContract;
import vn.travel.booking.util.constant.ContractStatus;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface HostContractRepository extends JpaRepository<HostContract, Long>, JpaSpecificationExecutor<HostContract> {

    // Hosts can view their own contracts.
    Page<HostContract> findByHost_Id(Long hostId, Pageable pageable);

    // Check the ACTIVE contract.
    boolean existsByHost_IdAndStatusIn(Long hostId, Collection<ContractStatus> statuses);

    boolean existsByHost_IdAndStatus(Long hostId, ContractStatus status);

    Optional<HostContract> findByIdAndHost_Id(Long id, Long hostId);

    // Find expired active contracts
    @Query("""
        SELECT c FROM HostContract c
        WHERE c.status = 'ACTIVE'
          AND c.endDate < :now
    """)
    List<HostContract> findExpiredActiveContracts(LocalDate now);

    @Query("""
        SELECT c FROM HostContract c
        WHERE c.status = 'ACTIVE'
          AND c.endDate >= :periodTo
    """)
    List<HostContract> findActiveContractsValidForPayout(
            LocalDate periodTo
    );

    List<HostContract> findByStatus(ContractStatus status);

}
