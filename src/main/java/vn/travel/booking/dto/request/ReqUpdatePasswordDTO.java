package vn.travel.booking.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ReqUpdatePasswordDTO {
    @NotBlank
    private String oldPassword;

    @NotBlank
    private String newPassword;
}
