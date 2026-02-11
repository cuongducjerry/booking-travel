package vn.travel.booking.dto.response.property;

import lombok.*;
import vn.travel.booking.dto.response.propertyimage.ResPropertyImage;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Data
public class ResPropertyDTO {

    private Long id;

    private String title;
    private String description;
    private String address;
    private String city;

    private double pricePerNight;
    private String currency;
    private int maxGuests;

    // property type
    private Long propertyTypeId;
    private String propertyTypeName;
    private List<ResPropertyImage> images;
    private List<ResPropertyBookingDTO> bookings;
    private List<ResPropertyDetailDTO.AmenityDTO> amenities;

    private List<ResPropertyImage> imageDrafts;

    // host
    private Long hostId;
    private String hostName;
    private Long contractId;

    private String status;   // DRAFT

    private Instant createdAt;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ResPropertyBookingDTO {
        private LocalDate checkIn;
        private LocalDate checkOut;
    }

    private Double latitude;
    private Double longitude;

    private boolean hasActiveContract;

}
