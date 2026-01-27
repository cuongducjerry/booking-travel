package vn.travel.booking.mapper;

import org.springframework.stereotype.Component;
import vn.travel.booking.dto.response.fee.ResHostFeeDTO;
import vn.travel.booking.entity.HostFee;

@Component
public class FeeMapper {

    public ResHostFeeDTO convertResHostFeeDTO(HostFee fee) {
        ResHostFeeDTO dto = new ResHostFeeDTO();
        dto.setId(fee.getId());
        dto.setBookingId(fee.getBookingId());
        dto.setAmount(fee.getAmount());
        dto.setRate(fee.getRate());
        dto.setStatus(fee.getStatus());
        dto.setDueAt(fee.getDueAt());
        dto.setPaidAt(fee.getPaidAt());
        return dto;
    }

}
