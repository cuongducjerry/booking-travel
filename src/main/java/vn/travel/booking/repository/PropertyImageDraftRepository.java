package vn.travel.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.travel.booking.entity.PropertyImageDraft;

import java.util.List;

@Repository
public interface PropertyImageDraftRepository extends JpaRepository<PropertyImageDraft, Long> {

    List<PropertyImageDraft> findByProperty_Id(Long propertyId);

    void deleteByProperty_Id(Long propertyId);
}
