package vn.travel.booking.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.travel.booking.entity.Booking;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    // User's booking
    Page<Booking> findByUser_Id(Long userId, Pageable pageable);

    // Host's booking
    @Query("""
        SELECT b FROM Booking b
        WHERE b.property.host.id = :hostId
    """)
    List<Booking> findByHostId(Long hostId);

    // Check for duplicate dates
    @Query("""
        SELECT COUNT(b) > 0 FROM Booking b
        WHERE b.property.id = :propertyId
          AND b.status IN ('NEW', 'CONFIRMED')
          AND b.checkIn < :checkOut
          AND b.checkOut > :checkIn
    """)
    boolean existsOverlappingBooking(
            Long propertyId,
            LocalDate checkIn,
            LocalDate checkOut
    );

}
