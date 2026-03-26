package vn.travel.booking.repository;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.travel.booking.entity.Role;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long>, JpaSpecificationExecutor<Role> {

    Optional<Role> findById(Long id);

    boolean existsByNameIgnoreCase(String name);

    Role findByName(String name);

    @Query("SELECT r FROM Role r WHERE r.name = :name")
    Optional<Role> findByNameIncludeInactive(@Param("name") String name);

}
