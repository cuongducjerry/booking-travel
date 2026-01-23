package vn.travel.booking.dto.request.booking;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ResCreateBookingDTO {
    private Long propertyId;   // ID room / property
    private LocalDate checkIn; // check-in date
    private LocalDate checkOut; // check-out date
}
