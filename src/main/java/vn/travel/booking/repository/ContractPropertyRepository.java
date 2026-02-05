package vn.travel.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.travel.booking.entity.ContractProperty;
import vn.travel.booking.util.constant.ContractStatus;

import java.util.List;

@Repository
public interface ContractPropertyRepository extends JpaRepository<ContractProperty, Long> {

    @Query("""
        SELECT COUNT(cp) > 0
        FROM ContractProperty cp
        JOIN cp.contract c
        WHERE c.status = 'ACTIVE'
          AND cp.property.id IN :propertyIds
    """)
    boolean existsActiveContractByPropertyIds(
            @Param("propertyIds") List<Long> propertyIds
    );

    @Query("""
        select count(cp) > 0
        from ContractProperty cp
        where cp.property.id = :propertyId
          and cp.contract.status = :status
    """)
    boolean existsByPropertyIdAndContractStatus(
            @Param("propertyId") Long propertyId,
            @Param("status") ContractStatus status
    );

}
