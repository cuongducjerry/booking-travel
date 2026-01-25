package vn.travel.booking.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.travel.booking.entity.Booking;

import java.time.Instant;
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

    @Query("""
        SELECT b FROM Booking b
        WHERE b.status = 'DONE'
          AND b.property.host.id = :hostId
          AND b.property.contract.id = :contractId
          AND b.checkOut >= :from
          AND b.checkOut < :to
          AND NOT EXISTS (
              SELECT 1 FROM HostPayoutItem i
              WHERE i.booking = b
          )
    """)
    List<Booking> findDoneBookingsNotPayoutYet(
            Long hostId,
            Long contractId,
            LocalDate from,
            LocalDate to
    );

    @Query("""
        SELECT b FROM Booking b
        WHERE b.status = 'DONE'
          AND b.property.host.id = :hostId
          AND b.checkOut BETWEEN :from AND :to
          AND NOT EXISTS (
              SELECT 1 FROM HostPayoutItem i WHERE i.booking = b
          )
    """)
    List<Booking> findDoneBookingsForMonthlyPayout(
            Long hostId,
            LocalDate from,
            LocalDate to
    );

}
