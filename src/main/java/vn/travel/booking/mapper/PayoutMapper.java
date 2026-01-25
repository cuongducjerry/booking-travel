package vn.travel.booking.mapper;

import org.springframework.stereotype.Component;
import vn.travel.booking.dto.response.payout.ResHostPayoutDTO;
import vn.travel.booking.entity.HostPayout;

@Component
public class PayoutMapper {

    public ResHostPayoutDTO convertToResHostPayoutDTO(HostPayout p) {
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
                .build();
    }

}
