package vn.travel.booking.controller.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vn.travel.booking.dto.response.ResultPaginationDTO;
import vn.travel.booking.service.AdminBookingService;
import vn.travel.booking.util.annotation.ApiMessage;
import vn.travel.booking.util.constant.BookingStatus;

@RestController
@RequestMapping("/api/v1/admin/bookings")
@RequiredArgsConstructor
public class AdminBookingController {

    private final AdminBookingService adminBookingService;

    // =========================
    // ADMIN VIEW LIST
    // =========================
    @GetMapping
    @PreAuthorize("hasAnyAuthority('BOOKING_LIST_ALL')")
    @ApiMessage("Admin view booking list")
    public ResponseEntity<ResultPaginationDTO> getBookings(
            @RequestParam(required = false) BookingStatus status,
            Pageable pageable
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(adminBookingService.getBookings(status, pageable));
    }
}

