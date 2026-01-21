package vn.travel.booking.dto.request.property;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import vn.travel.booking.util.constant.PropertyStatus;

@Data
public class ReqPropertyDecisionDTO {

    @NotNull
    private PropertyStatus decision;
    // APPROVED | REJECTED

    private String reason;
}

