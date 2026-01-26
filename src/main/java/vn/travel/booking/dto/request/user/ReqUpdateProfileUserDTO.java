package vn.travel.booking.dto.request.user;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ReqUpdateProfileUserDTO {

    @NotNull(message = "ID người dùng không được để trống")
    private Long id;

    @NotBlank(message = "Họ tên không được để trống")
    @Size(max = 100, message = "Họ tên không được vượt quá 100 ký tự")
    private String fullName;

    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(
            regexp = "^(0[0-9]{9}|\\+84[0-9]{9})$",
            message = "Số điện thoại không đúng định dạng"
    )
    private String phone;

    @Size(max = 500, message = "Giới thiệu bản thân không được vượt quá 500 ký tự")
    private String bio;

    @Size(max = 255, message = "Địa chỉ không được vượt quá 255 ký tự")
    private String address;

    @Min(value = 0, message = "Tuổi không được nhỏ hơn 0")
    @Max(value = 120, message = "Tuổi không được lớn hơn 120")
    private int age;
}

