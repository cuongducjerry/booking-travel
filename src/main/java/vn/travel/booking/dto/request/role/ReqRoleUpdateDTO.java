package vn.travel.booking.dto.request.role;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.Instant;

@Data
public class ReqRoleUpdateDTO {

    @NotNull(message = "id không được để trống")
    @Positive(message = "id không hợp lệ")
    private Long id;

    @NotBlank(message = "name không được để trống")
    @Size(max = 50, message = "name tối đa 50 ký tự")
    private String name;

    @Size(max = 255, message = "description tối đa 255 ký tự")
    private String description;
}

