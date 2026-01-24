package vn.travel.booking.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import vn.travel.booking.dto.response.payment.ResCallBackPayDTO;
import vn.travel.booking.dto.response.payment.ResCreatePaymentDTO;
import vn.travel.booking.entity.Booking;
import vn.travel.booking.entity.Payment;
import vn.travel.booking.mapper.PaymentMapper;
import vn.travel.booking.repository.BookingRepository;
import vn.travel.booking.repository.PaymentRepository;
import vn.travel.booking.util.constant.BookingStatus;
import vn.travel.booking.util.constant.PaymentMethod;
import vn.travel.booking.util.constant.PaymentStatus;
import vn.travel.booking.util.error.BusinessException;
import vn.travel.booking.util.error.IdInvalidException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;
//    private final VNPayService vnPayService;
    private final PaymentMapper paymentMapper;

    public PaymentService(
            PaymentRepository paymentRepository,
            BookingRepository bookingRepository,
//            VNPayService vnPayService,
            PaymentMapper paymentMapper
    ) {
        this.paymentRepository = paymentRepository;
        this.bookingRepository = bookingRepository;
//        this.vnPayService = vnPayService;
        this.paymentMapper = paymentMapper;
    }

    /* ========== CREATE PAYMENT ========== */
    @Transactional
    public ResCreatePaymentDTO createPayment(Long bookingId, HttpServletRequest request) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() ->
                        new IdInvalidException("Booking với id = " + bookingId + " không tồn tại"));

        String txnRef = UUID.randomUUID().toString().replace("-", "");

        Payment payment = Payment.builder()
                .booking(booking)
                .amount(booking.getGrossAmount())
                .currency(booking.getCurrency())
                .paymentMethod(PaymentMethod.VNPAY)
                .providerTxnId(txnRef)
                .status(PaymentStatus.PENDING)
                .build();

        paymentRepository.save(payment);

//        String urlPay = vnPayService.createPaymentUrl(payment, request);
        return paymentMapper.toResCreatePaymentDTO("Tạo payment url vnpay thành công!");
    }

    @Transactional
    public ResCreatePaymentDTO createCashPayment(Long bookingId) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() ->
                        new IdInvalidException("Booking với id = " + bookingId + " không tồn tại"));

        // optional: Block if payment has already been successfully received for the booking.
        boolean hasPaid = paymentRepository
                .existsByBooking_IdAndStatus(bookingId, PaymentStatus.SUCCESS);

        if (hasPaid) {
            throw new BusinessException("Booking đã được thanh toán");
        }

        Payment payment = Payment.builder()
                .booking(booking)
                .amount(booking.getGrossAmount())
                .currency(booking.getCurrency())
                .paymentMethod(PaymentMethod.CASH)
                .status(PaymentStatus.PENDING) // Wait to pay upon arrival.
                .build();

        paymentRepository.save(payment);

        booking.setStatus(BookingStatus.CONFIRMED);
        bookingRepository.save(booking);

        return paymentMapper.toResCreatePaymentDTO(
                "Đã tạo booking - Thanh toán khi đến"
        );
    }


    /* ========== CALLBACK HANDLER ========== */
//    @Transactional
//    public ResCallBackPayDTO vnpayCallback(HttpServletRequest request) {
//
//        Map<String, String> params = new HashMap<>();
//        Enumeration<String> names = request.getParameterNames();
//
//        while (names.hasMoreElements()) {
//            String name = names.nextElement();
//            if (!"vnp_SecureHash".equals(name)
//                    && !"vnp_SecureHashType".equals(name)) {
//                params.put(name, request.getParameter(name));
//            }
//        }
//
//        String secureHash = request.getParameter("vnp_SecureHash");
//
//        // 1. Verify signature
//        if (!vnPayService.verifyCallback(params, secureHash)) {
//            return paymentMapper.toResCallBackPayDTO("INVALID SIGNATURE");
//        }
//
//        String txnRef = request.getParameter("vnp_TxnRef");
//        String responseCode = request.getParameter("vnp_ResponseCode");
//
//        Payment payment = paymentRepository.findByProviderTxnId(txnRef)
//                .orElseThrow(() -> new RuntimeException("Payment not found"));
//
//        // 2. Idempotent callback
//        if (payment.getStatus() == PaymentStatus.SUCCESS) {
//            return paymentMapper.toResCallBackPayDTO("ALREADY PROCESSED");
//        }
//
//        // 3. Verify amount
//        long vnpAmount = Long.parseLong(request.getParameter("vnp_Amount"));
//        long expectedAmount = Math.round(payment.getAmount() * 100);
//        if (vnpAmount != expectedAmount) {
//            throw new RuntimeException("INVALID AMOUNT");
//        }
//        String transactionStatus = request.getParameter("vnp_TransactionStatus");
//
//        // 4. Handle result
//        if ("00".equals(responseCode) && "00".equals(transactionStatus)) {
//            payment.setStatus(PaymentStatus.SUCCESS);
//
//            Booking booking = payment.getBooking();
//            booking.setStatus("CONFIRMED");
//
//            bookingRepository.save(booking);
//            paymentRepository.save(payment);
//
//            return paymentMapper.toResCallBackPayDTO("PAYMENT SUCCESS");
//        }
//
//        payment.setStatus(PaymentStatus.FAILED);
//        paymentRepository.save(payment);
//        return paymentMapper.toResCallBackPayDTO("PAYMENT FAILED");
//    }

    @Transactional
    public ResCallBackPayDTO mockCallback(Long paymentId, boolean success) {

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new BusinessException("Payment not found"));

        if (payment.getStatus() == PaymentStatus.SUCCESS) {
            return paymentMapper.toResCallBackPayDTO("ALREADY PROCESSED");
        }

        if (success) {
            payment.setStatus(PaymentStatus.SUCCESS);

            Booking booking = payment.getBooking();
            booking.setStatus(BookingStatus.CONFIRMED);

            bookingRepository.save(booking);
            paymentRepository.save(payment);

            return paymentMapper.toResCallBackPayDTO("MOCK PAYMENT SUCCESS");
        }

        payment.setStatus(PaymentStatus.FAILED);
        paymentRepository.save(payment);
        return paymentMapper.toResCallBackPayDTO("MOCK PAYMENT FAILED");
    }

}