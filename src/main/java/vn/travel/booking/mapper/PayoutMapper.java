package vn.travel.booking.mapper;

import org.springframework.stereotype.Component;
import vn.travel.booking.dto.response.payout.ResHostPayoutDTO;
import vn.travel.booking.entity.HostPayout;

import java.util.List;

@Component
public class PayoutMapper {

    public ResHostPayoutDTO convertToResHostPayoutDTO(HostPayout p) {

        List<ResHostPayoutDTO.HostPayoutItemDTO> items =
                p.getItems() == null ? List.of() :
                        p.getItems().stream()
                                .map(item -> ResHostPayoutDTO.HostPayoutItemDTO.builder()
                                        .id(item.getId())
                                        .bookingId(item.getBooking().getId())
                                        .bookingAmount(item.getBookingAmount())
                                        .commissionFee(item.getCommissionFee())
                                        .netAmount(item.getNetAmount())
                                        .build())
                                .toList();

        return ResHostPayoutDTO.builder()
                .id(p.getId())
                .hostId(p.getHost().getId())
                .contractId(p.getContract().getId())
                .periodFrom(p.getPeriodFrom())
                .periodTo(p.getPeriodTo())
                .grossAmount(p.getGrossAmount())
                .commissionFee(p.getCommissionFee())
                .netAmount(p.getNetAmount())
                .status(p.getStatus())

                /* audit */
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())

                /* paid */
                .paidAt(p.getPaidAt())
                .transactionRef(p.getTransactionRef())

                /* rejected */
                .rejectedAt(p.getRejectedAt())
                .rejectReason(p.getRejectReason())

                .items(items)
                .build();
    }

}
