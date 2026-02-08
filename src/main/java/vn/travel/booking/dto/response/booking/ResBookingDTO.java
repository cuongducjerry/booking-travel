package vn.travel.booking.dto.response.booking;

import lombok.AllArgsConstructor;
import lombok.Data;
import vn.travel.booking.util.constant.BookingStatus;

import java.time.Instant;
import java.time.LocalDate;

@Data
public class ResBookingDTO {
    private Long id;

    private LocalDate checkIn;
    private LocalDate checkOut;
    private int nights;

    private double pricePerNightSnapshot;
    private String currency;

    private double grossAmount;
    private double commissionRate;
    private double commissionFee;
    private double hostEarning;

    private BookingStatus status; // NEW, CONFIRMED, CANCELLED, DONE

    private Instant createdAt;
    private Instant updatedAt;

    // ---- User info  ----
    private Long userId;
    private String userName;
    private String userEmail;

    // ---- Property info ----
    private Long propertyId;
    private String propertyName;

    // ---- Review info (NEW) ----
    private ReviewSummary review;

    @Data
    @AllArgsConstructor
    public static class ReviewSummary {
        private Long id;
        private int rating;
        private String comment;
    }
}
