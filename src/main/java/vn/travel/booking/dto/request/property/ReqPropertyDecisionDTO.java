package vn.travel.booking.dto.request.property;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import vn.travel.booking.util.constant.PropertyStatus;

@Data
public class ReqPropertyDecisionDTO {

    @NotNull(message = "decision không được để trống")
    private PropertyStatus decision;
    // APPROVED | REJECTED

    @Size(max = 500, message = "reason tối đa 500 ký tự")
    private String reason;

    @AssertTrue(message = "reason là bắt buộc khi REJECTED")
    public boolean isReasonValid() {
        if (decision == PropertyStatus.REJECTED) {
            return reason != null && !reason.isBlank();
        }
        return true;
    }
}

