package vn.travel.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import vn.travel.booking.entity.PropertyImage;

@Repository
public interface PropertyImageRepository extends JpaRepository<PropertyImage, Long>, JpaSpecificationExecutor<PropertyImage> {
    void deleteByImageUrlAndProperty_Id(String imageUrl, Long propertyId);
}
