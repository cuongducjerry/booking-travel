package vn.travel.booking.dto.request.payout;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.Instant;
import java.time.LocalDate;

@Data
public class ReqCreateHostPayoutDTO {

    @NotNull(message = "hostId không được để trống")
    private Long hostId;

    @NotNull(message = "contractId không được để trống")
    private Long contractId;

    @NotNull(message = "periodFrom không được để trống")
    private LocalDate periodFrom;

    @NotNull(message = "periodTo không được để trống")
    private LocalDate periodTo;

    @AssertTrue(message = "periodTo phải sau hoặc bằng periodFrom")
    public boolean isValidPeriod() {
        if (periodFrom == null || periodTo == null) return true;
        return !periodTo.isBefore(periodFrom);
    }
}


