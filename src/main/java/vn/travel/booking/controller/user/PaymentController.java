package vn.travel.booking.controller.user;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vn.travel.booking.dto.response.payment.ResCallBackPayDTO;
import vn.travel.booking.dto.response.payment.ResCreatePaymentDTO;
import vn.travel.booking.service.PaymentService;
import vn.travel.booking.util.annotation.ApiMessage;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/{bookingId}/vnpay")
    @PreAuthorize("hasAuthority('PAYMENT_CREATE')")
    @ApiMessage("Create a new vnpay payment")
    public ResponseEntity<ResCreatePaymentDTO> pay(
            @PathVariable Long bookingId,
            HttpServletRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(paymentService.createPayment(bookingId, request));
    }

    @PostMapping("/{bookingId}/pay-at-property")
    @PreAuthorize("hasAuthority('PAYMENT_CREATE')")
    @ApiMessage("Create cash payment")
    public ResponseEntity<ResCreatePaymentDTO> payAtProperty(
            @PathVariable Long bookingId
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(paymentService.createCashPayment(bookingId));
    }


//    @GetMapping("/vnpay-callback")
//    @PreAuthorize("hasAuthority('PAYMENT_CALLBACK')")
//    @ApiMessage("Check callback a payment")
//    public ResponseEntity<ResCallBackPayDTO> callback(HttpServletRequest request) {
//        return ResponseEntity.status(HttpStatus.OK).body(paymentService.vnpayCallback(request));
//    }

    @PostMapping("/mock-callback")
    @PreAuthorize("hasAuthority('PAYMENT_CALLBACK')")
    @ApiMessage("Mooc callback a payment")
    public ResCallBackPayDTO mockPayment(
            @RequestParam Long paymentId,
            @RequestParam boolean success
    ) {
        return paymentService.mockCallback(paymentId, success);
    }

}
