package vn.travel.booking.dto.response.payment;

import lombok.Data;

@Data
public class ResCallBackPayDTO {
    private Long paymentId;
    private String message;
}
