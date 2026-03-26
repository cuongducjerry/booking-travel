package vn.travel.booking.repository;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.travel.booking.entity.Permission;

import java.util.List;
import java.util.Optional;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long>, JpaSpecificationExecutor<Permission> {

    Optional<Permission> findByCode(String code);

    List<Permission> findByActiveTrue();

    List<Permission> findByIdIn(List<Long> ids);

    @Query("SELECT p FROM Permission p WHERE p.code = :code")
    Optional<Permission> findByCodeIncludeInactive(@Param("code") String code);
}
