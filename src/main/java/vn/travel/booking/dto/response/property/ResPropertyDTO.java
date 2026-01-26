package vn.travel.booking.dto.response.property;

import lombok.Data;

import java.time.Instant;

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

    // host
    private Long hostId;
    private String hostName;

    private String status;   // DRAFT

    private Instant createdAt;

}
