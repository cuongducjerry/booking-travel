package vn.travel.booking.dto.response.property;

import lombok.Data;

@Data
public class ResPropertyWishlistDTO {
    private Long propertyId;
    private String propertyName;
    private String address;
    private String imageUrl;
}
