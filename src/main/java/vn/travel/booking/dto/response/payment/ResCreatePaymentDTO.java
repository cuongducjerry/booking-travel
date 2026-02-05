package vn.travel.booking.dto.response.payment;

import lombok.Data;

@Data
public class ResCreatePaymentDTO {
    private Long paymentId;
    private String urlPay;
}
