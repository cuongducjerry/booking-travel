package vn.travel.booking.dto.request.property;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class ReqPropertyAmenityDTO {
    @NotEmpty(message = "amenityIds không được để trống")
    private List<@NotNull(message = "amenityId không hợp lệ") Long> amenityIds;
}
