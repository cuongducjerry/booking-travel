package vn.travel.booking.dto.request.amenity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ReqUpdateAmenityDTO {

    @NotNull(message = "id không được để trống")
    @Positive(message = "id phải là số dương")
    private Long id;

    @NotBlank(message = "Tên tiện nghi không được để trống")
    @Size(max = 100, message = "Tên tiện nghi tối đa 100 ký tự")
    private String name;

    @NotBlank(message = "Icon không được để trống")
    @Size(max = 255, message = "Icon tối đa 255 ký tự")
    private String icon;
}
