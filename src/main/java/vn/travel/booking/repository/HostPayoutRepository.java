package vn.travel.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.travel.booking.entity.HostContract;
import vn.travel.booking.entity.HostPayout;
import vn.travel.booking.util.constant.PayoutStatus;

import java.time.Instant;
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

    @Query("""
        SELECT 
            COALESCE(SUM(p.grossAmount), 0),
            COALESCE(SUM(p.commissionFee), 0),
            COALESCE(SUM(p.netAmount), 0)
        FROM HostPayout p
        WHERE p.status = :status
    """)
    List<Object[]> getRevenueSummary(@Param("status") PayoutStatus status);

    @Query("""
        SELECT 
            YEAR(p.paidAt),
            MONTH(p.paidAt),
            SUM(p.grossAmount),
            SUM(p.commissionFee),
            SUM(p.netAmount)
        FROM HostPayout p
        WHERE p.status = 'PAID'
          AND p.paidAt >= :fromDate
        GROUP BY YEAR(p.paidAt), MONTH(p.paidAt)
        ORDER BY YEAR(p.paidAt), MONTH(p.paidAt)
    """)
    List<Object[]> getRevenueLast12Months(@Param("fromDate") Instant fromDate);

    @Query("""
        SELECT 
            COALESCE(SUM(p.grossAmount), 0.0),
            COALESCE(SUM(p.commissionFee), 0.0),
            COALESCE(SUM(p.netAmount), 0.0)
        FROM HostPayout p
        WHERE p.host.id = :hostId
    """)
    List<Object[]> sumRevenueByHost(@Param("hostId") Long hostId);

    @Query("""
        SELECT 
            YEAR(p.paidAt),
            MONTH(p.paidAt),
            SUM(p.grossAmount),
            SUM(p.commissionFee),
            SUM(p.netAmount)
        FROM HostPayout p
        WHERE p.status = :status
          AND p.host.id = :hostId
          AND p.paidAt >= :fromDate
        GROUP BY YEAR(p.paidAt), MONTH(p.paidAt)
        ORDER BY YEAR(p.paidAt), MONTH(p.paidAt)
    """)
    List<Object[]> getRevenueLast12MonthsByHost(
            @Param("hostId") Long hostId,
            @Param("fromDate") Instant fromDate,
            @Param("status") PayoutStatus status
    );

}

