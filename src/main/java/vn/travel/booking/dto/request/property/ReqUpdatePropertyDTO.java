package vn.travel.booking.dto.request.property;

import lombok.Data;

@Data
public class ReqUpdatePropertyDTO {
    private String title;
    private String description;
    private String address;
    private String city;
    private double pricePerNight;
    private String currency;
    private int maxGuests;
    private Long propertyTypeId;
}
