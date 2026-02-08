package vn.travel.booking.dto.response.payout;

import lombok.Builder;
import lombok.*;
import vn.travel.booking.util.constant.PayoutStatus;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class ResHostPayoutDTO {

    private Long id;
    private Long hostId;
    private Long contractId;

    private LocalDate periodFrom;
    private LocalDate periodTo;

    private double grossAmount;
    private double commissionFee;
    private double netAmount;

    private PayoutStatus status;

    /* ===== audit ===== */
    private Instant createdAt;
    private Instant updatedAt;

    /* ===== paid ===== */
    private Instant paidAt;
    private String transactionRef;

    /* ===== rejected ===== */
    private Instant rejectedAt;
    private String rejectReason;

    @Builder.Default
    private List<HostPayoutItemDTO> items = new ArrayList<>();

    /* ================= ITEM DTO ================= */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class HostPayoutItemDTO {
        private Long id;
        private Long bookingId;
        private double bookingAmount;
        private double commissionFee;
        private double netAmount;
    }
}

