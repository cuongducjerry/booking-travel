package vn.travel.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import vn.travel.booking.entity.Role;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long>, JpaSpecificationExecutor<Role> {

    Optional<Role> findById(Long id);

    boolean existsByNameIgnoreCase(String name);

    Role findByName(String name);

}
