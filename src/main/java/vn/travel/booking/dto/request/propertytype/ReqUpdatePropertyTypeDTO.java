package vn.travel.booking.dto.request.propertytype;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ReqUpdatePropertyTypeDTO {

    @NotNull(message = "id không được để trống")
    @Positive(message = "id không hợp lệ")
    private Long id;

    @NotBlank(message = "name không được để trống")
    @Size(max = 100, message = "name tối đa 100 ký tự")
    private String name;
}

