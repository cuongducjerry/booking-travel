package vn.travel.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import vn.travel.booking.entity.PropertyType;

import java.util.Optional;

@Repository
public interface PropertyTypeRepository extends JpaRepository<PropertyType, Long>, JpaSpecificationExecutor<PropertyType> {

    Optional<PropertyType> findById(Long id);

    boolean existsByNameIgnoreCase(String name);

}
