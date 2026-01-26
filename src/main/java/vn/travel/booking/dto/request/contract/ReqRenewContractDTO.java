package vn.travel.booking.dto.request.contract;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.Instant;
import java.time.LocalDate;

@Data
public class ReqRenewContractDTO {
    @NotNull(message = "Ngày kết thúc mới không được để trống")
    @Future(message = "Ngày kết thúc mới phải là ngày trong tương lai")
    private LocalDate newEndDate;

    @NotNull(message = "Tỷ lệ hoa hồng không được để trống")
    @DecimalMin(value = "0.01", message = "Hoa hồng phải > 0")
    @DecimalMax(value = "1.00", message = "Hoa hồng tối đa 100%")
    private double expectedCommissionRate;
}
