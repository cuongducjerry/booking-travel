package vn.travel.booking.dto.request.propertytype;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ReqCreatePropertyTypeDTO {

    @NotBlank(message = "name không được để trống")
    @Size(max = 100, message = "name tối đa 100 ký tự")
    private String name;
}
