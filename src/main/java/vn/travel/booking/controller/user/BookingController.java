package vn.travel.booking.controller.user;

import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vn.travel.booking.dto.request.booking.ReqCreateBookingDTO;
import vn.travel.booking.dto.response.ResultPaginationDTO;
import vn.travel.booking.dto.response.booking.ResBookingDTO;
import vn.travel.booking.service.BookingService;
import vn.travel.booking.util.annotation.ApiMessage;

@RestController
@RequestMapping("/api/v1/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('BOOKING_CREATE')")
    @ApiMessage("Create a new booking")
    public ResponseEntity<ResBookingDTO> create(@Valid @RequestBody ReqCreateBookingDTO req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bookingService.createBooking(req));
    }

    @GetMapping("/my-booking")
    @PreAuthorize("hasAuthority('BOOKING_LIST_PERSONAL')")
    @ApiMessage("Retrieve your own booking")
    public ResponseEntity<ResultPaginationDTO> myBookings(Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK).body(bookingService.getMyBookings(pageable));
    }

    @PutMapping("/{bookingId}/cancel")
    @PreAuthorize("hasAuthority('BOOKING_CANCEL')")
    @ApiMessage("Cancel booking")
    public ResponseEntity<Void> cancel(@PathVariable Long bookingId) {
        bookingService.requestCancelBooking(bookingId);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    @DeleteMapping("/{bookingId}")
    @PreAuthorize("hasAuthority('BOOKING_DELETE')")
    @ApiMessage("Delete booking")
    public ResponseEntity<Void> delete(@PathVariable Long bookingId) {
        bookingService.deleteBooking(bookingId);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    @GetMapping("/{bookingId}")
    @PreAuthorize("hasAuthority('BOOKING_VIEW_DETAIL')")
    @ApiMessage("Retrieve booking detail")
    public ResponseEntity<ResBookingDTO> getDetail(@PathVariable Long bookingId) {
        return ResponseEntity.ok(bookingService.getBookingDetail(bookingId));
    }

}
