package vn.travel.booking.dto.request.contract;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Data
public class ReqHostContractRequestDTO {

    @NotNull(message = "Tỷ lệ hoa hồng không được để trống")
    @DecimalMin(value = "0.01", message = "Hoa hồng phải > 0")
    @DecimalMax(value = "1.00", message = "Hoa hồng tối đa 100%")
    private double expectedCommissionRate;
    // 0.15 = 15%

    @NotNull(message = "Ngày bắt đầu không được để trống")
    @FutureOrPresent(message = "Ngày bắt đầu phải từ hôm nay trở đi")
    private LocalDate startDate;

    @NotNull(message = "Ngày kết thúc không được để trống")
    @Future(message = "Ngày kết thúc phải là ngày trong tương lai")
    private LocalDate endDate;
}

