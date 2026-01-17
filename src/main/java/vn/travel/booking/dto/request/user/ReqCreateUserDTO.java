package vn.travel.booking.dto.request.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
public class ReqCreateUserDTO {

    @Email(message = "Email không hợp lệ")
    @NotBlank(message = "email không được để trống")
    private String email;

    @NotBlank(message = "password không được để trống")
    private String password;

    private String fullName;
    private String phone;
    private String address;
    private int age;
    private boolean active = true;

    private Role role;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Role {
        private long id;
    }
}
