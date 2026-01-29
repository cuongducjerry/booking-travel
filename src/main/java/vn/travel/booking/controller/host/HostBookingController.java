package vn.travel.booking.controller.host;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vn.travel.booking.dto.response.ResultPaginationDTO;
import vn.travel.booking.service.BookingHostService;
import vn.travel.booking.util.annotation.ApiMessage;
import vn.travel.booking.util.constant.BookingStatus;

@RestController
@RequestMapping("/api/v1/host/bookings")
@RequiredArgsConstructor
public class HostBookingController {

    private final BookingHostService bookingHostService;

    // HOST confirm
    @PutMapping("/{id}/confirm")
    @PreAuthorize("hasAuthority('BOOKING_CONFIRM')")
    @ApiMessage("Confirm booking from host")
    public ResponseEntity<Void> confirm(@PathVariable Long id) {
        bookingHostService.confirmBooking(id);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    // Cancel (user / host)
    @PutMapping("/{id}/cancel")
    @PreAuthorize("hasAuthority('BOOKING_CANCEL')")
    @ApiMessage("Cancel booking from host")
    public ResponseEntity<Void> cancel(@PathVariable Long id) {
        bookingHostService.cancelBookingByHost(id);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    // Done (host / system)
    @PutMapping("/{id}/done")
    @PreAuthorize("hasAuthority('BOOKING_DONE')")
    @ApiMessage("Done booking from host")
    public ResponseEntity<Void> done(@PathVariable Long id) {
        bookingHostService.doneBooking(id);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    // =========================
    // HOST VIEW LIST
    // =========================
    @GetMapping
    @PreAuthorize("hasAuthority('BOOKING_LIST_OWN')")
    @ApiMessage("Host view bookings they manage")
    public ResponseEntity<ResultPaginationDTO> getHostBookings(
            @RequestParam(required = false) BookingStatus status,
            Pageable pageable
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(bookingHostService.getHostBookings(status, pageable));
    }

}

