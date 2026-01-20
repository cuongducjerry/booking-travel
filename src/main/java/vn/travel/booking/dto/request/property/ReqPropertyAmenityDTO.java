package vn.travel.booking.dto.request.property;

import lombok.Data;

import java.util.List;

@Data
public class ReqPropertyAmenityDTO {
    private List<Long> amenityIds;
}
