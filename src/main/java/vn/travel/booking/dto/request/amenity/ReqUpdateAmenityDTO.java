package vn.travel.booking.dto.request.amenity;

import lombok.Data;

@Data
public class ReqUpdateAmenityDTO {
    private long id;
    private String name;
    private String icon;
}
