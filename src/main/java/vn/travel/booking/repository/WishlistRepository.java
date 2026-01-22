package vn.travel.booking.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import vn.travel.booking.entity.User;
import vn.travel.booking.entity.Wishlist;

import java.util.List;
import java.util.Optional;

@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, Long>, JpaSpecificationExecutor<Wishlist> {

    Optional<Wishlist> findByUser_IdAndProperty_Id(Long userId, Long propertyId);

    Page<Wishlist> findByUser_IdAndActiveTrue(Long userId, Pageable pageable);

}
