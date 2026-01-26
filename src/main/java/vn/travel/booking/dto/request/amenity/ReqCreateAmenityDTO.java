package vn.travel.booking.dto.request.amenity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ReqCreateAmenityDTO {

    @NotBlank(message = "Tên tiện nghi không được để trống")
    @Size(max = 100, message = "Tên tiện nghi tối đa 100 ký tự")
    private String name;

    @NotBlank(message = "Icon không được để trống")
    @Size(max = 255, message = "Icon tối đa 255 ký tự")
    private String icon;
}
