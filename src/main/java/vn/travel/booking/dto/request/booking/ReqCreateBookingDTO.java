package vn.travel.booking.dto.request.booking;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ReqCreateBookingDTO {

    @NotNull(message = "propertyId không được để trống")
    @Positive(message = "propertyId phải là số dương")
    private Long propertyId;

    @NotNull(message = "checkIn không được để trống")
    @FutureOrPresent(message = "checkIn phải là hôm nay hoặc tương lai")
    private LocalDate checkIn;

    @NotNull(message = "checkOut không được để trống")
    @Future(message = "checkOut phải là ngày trong tương lai")
    private LocalDate checkOut;
}
