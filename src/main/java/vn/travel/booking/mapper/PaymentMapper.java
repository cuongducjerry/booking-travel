package vn.travel.booking.mapper;

import org.springframework.stereotype.Component;
import vn.travel.booking.dto.response.payment.ResCallBackPayDTO;
import vn.travel.booking.dto.response.payment.ResCreatePaymentDTO;
import vn.travel.booking.entity.Payment;

@Component
public class PaymentMapper {

    public ResCreatePaymentDTO toResCreatePaymentDTO(Long id, String url) {
        ResCreatePaymentDTO dto = new ResCreatePaymentDTO();
        dto.setPaymentId(id);
        dto.setUrlPay(url);
        return dto;
    }

    public ResCallBackPayDTO toResCallBackPayDTO(Long id, String message) {
        ResCallBackPayDTO dto = new ResCallBackPayDTO();
        dto.setPaymentId(id);
        dto.setMessage(message);
        return dto;
    }

}
