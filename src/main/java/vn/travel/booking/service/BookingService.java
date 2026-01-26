package vn.travel.booking.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.travel.booking.dto.request.booking.ReqCreateBookingDTO;
import vn.travel.booking.dto.response.ResultPaginationDTO;
import vn.travel.booking.dto.response.booking.ResBookingDTO;
import vn.travel.booking.entity.Booking;
import vn.travel.booking.entity.Property;
import vn.travel.booking.entity.User;
import vn.travel.booking.mapper.BookingMapper;
import vn.travel.booking.mapper.PaginationMapper;
import vn.travel.booking.repository.BookingRepository;
import vn.travel.booking.repository.PropertyRepository;
import vn.travel.booking.repository.UserRepository;
import vn.travel.booking.util.SecurityUtil;
import vn.travel.booking.util.constant.BookingStatus;
import vn.travel.booking.util.error.BusinessException;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final PropertyRepository propertyRepository;
    private final BookingMapper bookingMapper;
    private final PaginationMapper paginationMapper;

    public BookingService(
            BookingRepository bookingRepository,
            UserRepository userRepository,
            PropertyRepository propertyRepository,
            BookingMapper bookingMapper,
            PaginationMapper paginationMapper) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.propertyRepository = propertyRepository;
        this.bookingMapper = bookingMapper;
        this.paginationMapper = paginationMapper;
    }

    @Transactional
    public ResBookingDTO createBooking(ReqCreateBookingDTO req) {

        Long userId = SecurityUtil.getCurrentUserId();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User với id = " + userId + " không tồn tại!"));

        Property property = propertyRepository.findById(req.getPropertyId())
                .orElseThrow(() -> new RuntimeException("Property với id = " + req.getPropertyId() + " không tồn tại!"));

        // check date
        if (!req.getCheckIn().isBefore(req.getCheckOut())) {
            throw new RuntimeException("Ngày không hợp lệ");
        }

        // check for duplicate bookings
        boolean overlapped = bookingRepository.existsOverlappingBooking(
                property.getId(),
                req.getCheckIn(),
                req.getCheckOut()
        );
        if (overlapped) {
            throw new RuntimeException("Phòng đã có người đặt");
        }

        int nights = (int) ChronoUnit.DAYS.between(
                req.getCheckIn(),
                req.getCheckOut()
        );

        double price = property.getPricePerNight();
        double gross = nights * price;

        double commissionRate = property.getContract().getCommissionRate();
        double commissionFee = gross * commissionRate;

        Booking booking = Booking.builder()
                .user(user)
                .property(property)
                .checkIn(req.getCheckIn())
                .checkOut(req.getCheckOut())
                .nights(nights)
                .pricePerNightSnapshot(price)
                .grossAmount(gross)
                .commissionRate(commissionRate)
                .commissionFee(commissionFee)
                .hostEarning(gross - commissionFee)
                .currency(property.getCurrency())
                .status(BookingStatus.NEW)
                .active(true)
                .build();

        bookingRepository.save(booking);

        return this.bookingMapper.convertToResBookingDTO(booking);
    }

    @Transactional
    public void cancelBooking(Long bookingId) {

        Long userId = SecurityUtil.getCurrentUserId();

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking không tồn tại"));

        // Check that it's the right person.
        if (booking.getUser().getId() != userId) {
            throw new RuntimeException("Không có quyền hủy booking này");
        }

        // check status
        if (!booking.getStatus().equals(BookingStatus.NEW)
                && !booking.getStatus().equals(BookingStatus.CONFIRMED)) {
            throw new RuntimeException("Không thể hủy booking");
        }

        // check date
        if (!LocalDate.now().isBefore(booking.getCheckIn())) {
            throw new RuntimeException("Đã quá hạn hủy booking");
        }

        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);
    }

    @Transactional
    public void deleteBooking(Long bookingId) {

        Long userId = SecurityUtil.getCurrentUserId();

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking không tồn tại"));

        // không cho xóa booking đang chạy
        if (booking.getStatus().equals(BookingStatus.NEW)
                || booking.getStatus().equals(BookingStatus.CONFIRMED)) {
            throw new RuntimeException("Không thể xóa booking đang hoạt động");
        }

        // phân quyền
        if (!SecurityUtil.isAdmin() && booking.getUser().getId() != userId) {
            throw new RuntimeException("Không có quyền xóa");
        }

        bookingRepository.delete(booking);
    }

    public ResBookingDTO getBookingDetail(Long bookingId) {

        Long userId = SecurityUtil.getCurrentUserId();

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking không tồn tại"));

        // Permissions:
        // - ADMIN: View all bookings
        // - USER: View only their own bookings
        // - HOST: View only bookings for properties they own (if they have the host role)
        if (!SecurityUtil.isAdmin()) {

            // customer
            if (booking.getUser().getId() == userId) {
                // OK
            }
            // host
            else if (SecurityUtil.isHost() && booking.getProperty().getHost().getId() == (userId)) {
                // OK
            }
            else {
                throw new BusinessException("Không có quyền xem booking này");
            }
        }

        return bookingMapper.convertToResBookingDTO(booking);
    }


    public ResultPaginationDTO getMyBookings(Pageable pageable) {

        Long userId = SecurityUtil.getCurrentUserId();

        Page<Booking> pageResult = bookingRepository.findByUser_Id(userId, pageable);
        int pageNumber = pageable.getPageNumber() + 1;
        int pageSize = pageable.getPageSize();
        int totalPages = pageResult.getTotalPages();
        long totalElements = pageResult.getTotalElements();

        List<ResBookingDTO> bookings = pageResult.getContent()
                .stream()
                .map(item -> this.bookingMapper.convertToResBookingDTO(item))
                .toList();

        return this.paginationMapper.convertToResultPaginationDTO(
                pageNumber,
                pageSize,
                totalPages,
                totalElements,
                bookings);
    }

}
