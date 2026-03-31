package vn.travel.booking.dto.request.user;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Data
public class ReqCreateUserDTO {

    @Email(message = "Email không hợp lệ")
    @NotBlank(message = "email không được để trống")
    private String email;

    @NotBlank(message = "password không được để trống")
    @Size(min = 6, message = "password phải >= 6 ký tự")
    private String password;

    private String fullName;
    @Pattern(
            regexp = "^(0|\\+84)[0-9]{9}$",
            message = "Số điện thoại không hợp lệ"
    )
    private String phone;
    private String address;

    @NotNull(message = "Ngày sinh không được để trống")
    @Past(message = "Ngày sinh phải là ngày trong quá khứ")
    private LocalDate dateOfBirth;

    private boolean active = true;

    @Valid
    @NotNull(message = "role không được để trống")
    private Role role;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Role {
        @NotNull(message = "roleId không được để trống")
        private long id;
    }
}
