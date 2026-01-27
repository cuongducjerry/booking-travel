package vn.travel.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.travel.booking.entity.Booking;
import vn.travel.booking.entity.HostFee;
import vn.travel.booking.repository.BookingRepository;
import vn.travel.booking.repository.HostFeeRepository;
import vn.travel.booking.service.notification.NotificationService;
import vn.travel.booking.util.SecurityUtil;
import vn.travel.booking.util.constant.BookingStatus;
import vn.travel.booking.util.constant.FeeStatus;
import vn.travel.booking.util.constant.NotificationType;

import java.time.Instant;
import java.time.ZoneId;

@Service
@RequiredArgsConstructor
public class BookingHostService {

    private final BookingRepository bookingRepository;
    private final NotificationService notificationService;
    private final HostFeeRepository hostFeeRepository;

    @Transactional
    public void confirmBooking(Long bookingId) {

        Long hostId = SecurityUtil.getCurrentUserId();

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking không tồn tại"));

        // check ownership
        if (!booking.getProperty().getHost().getId().equals(hostId)) {
            throw new RuntimeException("Không có quyền confirm booking này");
        }

        if (booking.getStatus() != BookingStatus.NEW) {
            throw new RuntimeException("Chỉ có thể confirm booking NEW");
        }

        booking.setStatus(BookingStatus.CONFIRMED);

        // ================================
        // CREATE HOST FEE (CASH PAYMENT)
        // ================================
        if (!hostFeeRepository.existsByBookingId(booking.getId())) {

            Instant dueAt = booking.getCheckOut()
                    .plusDays(2)
                    .atStartOfDay(ZoneId.systemDefault())
                    .toInstant();

            HostFee fee = new HostFee();
            fee.setHostId(hostId);
            fee.setBookingId(booking.getId());
            fee.setAmount(booking.getCommissionFee());
            fee.setRate(booking.getCommissionRate());
            fee.setStatus(FeeStatus.PENDING);
            fee.setDueAt(dueAt);

            hostFeeRepository.save(fee);

            // notify HOST about fee obligation
            notificationService.notify(
                    hostId,
                    NotificationType.SYSTEM,
                    "Nhắc nhở thanh toán phí cho booking #" + booking.getId(),
                    "Bạn cần thanh toán phí dịch vụ cho booking phòng "
                            + booking.getProperty().getTitle()
                            + " ("
                            + booking.getCheckIn() + " → " + booking.getCheckOut()
                            + "). "
                            + "Hạn thanh toán: "
                            + dueAt.atZone(ZoneId.systemDefault()).toLocalDate()
                            + ". Quá hạn hệ thống sẽ khóa tài khoản host.",
                    true
            );
        }

        // notify CUSTOMER
        notificationService.notify(
                booking.getUser().getId(),
                NotificationType.BOOKING,
                "Booking #" + booking.getId() + " đã được xác nhận",
                "Host đã xác nhận booking phòng "
                        + booking.getProperty().getTitle()
                        + " ("
                        + booking.getCheckIn() + " → " + booking.getCheckOut()
                        + ")",
                true
        );
    }



    @Transactional
    public void doneBooking(Long bookingId) {

        Long hostId = SecurityUtil.getCurrentUserId();

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking không tồn tại"));

        if (!booking.getProperty().getHost().getId().equals(hostId)) {
            throw new RuntimeException("Không có quyền done booking này");
        }

        if (booking.getStatus() != BookingStatus.CONFIRMED) {
            throw new RuntimeException("Booking chưa được confirm");
        }

        booking.setStatus(BookingStatus.DONE);

        // notify customer
        notificationService.notify(
                booking.getUser().getId(),
                NotificationType.BOOKING,
                "Booking #" + booking.getId() + " đã hoàn thành",
                "Booking phòng "
                        + booking.getProperty().getTitle()
                        + " ("
                        + booking.getCheckIn() + " → " + booking.getCheckOut()
                        + ") đã hoàn thành. Cảm ơn bạn đã sử dụng dịch vụ!",
                false
        );

    }

    @Transactional
    public void cancelBookingByHost(Long bookingId) {

        Long hostId = SecurityUtil.getCurrentUserId();

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking không tồn tại"));

        // check ownership
        if (!booking.getProperty().getHost().getId().equals(hostId)) {
            throw new RuntimeException("Không có quyền");
        }

        BookingStatus status = booking.getStatus();

        // irrevocable state
        if (status == BookingStatus.DONE || status == BookingStatus.CANCELLED) {
            throw new RuntimeException("Không thể hủy booking này");
        }

        // Only allow the host to cancel in these states.
        if (status != BookingStatus.CANCEL_REQUESTED
                && status != BookingStatus.NEW
                && status != BookingStatus.CONFIRMED) {
            throw new RuntimeException("Trạng thái booking không hợp lệ để hủy");
        }

        booking.setStatus(BookingStatus.CANCELLED);

        // Contextual notifications
        if (status == BookingStatus.CANCEL_REQUESTED) {
            // host approve request
            notificationService.notify(
                    booking.getUser().getId(),
                    NotificationType.BOOKING,
                    "Booking đã được hủy",
                    "Host đã xác nhận yêu cầu hủy booking từ "
                            + booking.getCheckIn() + " đến " + booking.getCheckOut(),
                    true
            );
        } else {
            // Host proactively cancels
            notificationService.notify(
                    booking.getUser().getId(),
                    NotificationType.BOOKING,
                    "Booking bị hủy bởi host",
                    "Host đã hủy booking từ "
                            + booking.getCheckIn() + " đến " + booking.getCheckOut(),
                    true
            );
        }
    }



}

