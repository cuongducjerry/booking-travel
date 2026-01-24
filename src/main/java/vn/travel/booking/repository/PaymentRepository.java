package vn.travel.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.travel.booking.entity.Payment;
import vn.travel.booking.util.constant.PaymentStatus;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByProviderTxnId(String providerTxnId);

    List<Payment> findByBooking_Id(Long bookingId);

    boolean existsByBooking_IdAndStatus(Long bookingId, PaymentStatus status);
}
