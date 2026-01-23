package vn.travel.booking.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import vn.travel.booking.entity.Payment;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class VNPayService {

//    @Value("${vnpay.tmnCode}")
//    private String tmnCode;
//
//    @Value("${vnpay.hashSecret}")
//    private String hashSecret;
//
//    @Value("${vnpay.payUrl}")
//    private String payUrl;
//
//    @Value("${vnpay.returnUrl}")
//    private String returnUrl;
//
//    public String createPaymentUrl(Payment payment, HttpServletRequest request) {
//
//        Map<String, String> params = new HashMap<>();
//        params.put("vnp_Version", "2.1.0");
//        params.put("vnp_Command", "pay");
//        params.put("vnp_TmnCode", tmnCode);
//        long amount = Math.round(payment.getAmount() * 100);
//        params.put("vnp_Amount", String.valueOf(amount));
//        params.put("vnp_CurrCode", "VND");
//        params.put("vnp_TxnRef", payment.getProviderTxnId());
//        params.put("vnp_OrderInfo", "Thanh toan booking " + payment.getBooking().getId());
//        params.put("vnp_OrderType", "other");
//        params.put("vnp_ReturnUrl", returnUrl);
//        params.put("vnp_IpAddr", getClientIp(request));
//        params.put("vnp_Locale", "vn");
//
//        String createDate = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
//        params.put("vnp_CreateDate", createDate);
//
//        Calendar cal = Calendar.getInstance();
//        cal.add(Calendar.MINUTE, 15);
//        params.put("vnp_ExpireDate",
//                new SimpleDateFormat("yyyyMMddHHmmss").format(cal.getTime()));
//
//        String secureHash = hmacSHA512(hashSecret, buildHashData(params));
//        params.put("vnp_SecureHash", secureHash);
//
//        return payUrl + "?" + buildQuery(params);
//    }
//
//    /* ================= SUPPORT ================= */
//
//    public boolean verifyCallback(Map<String, String> params, String secureHash) {
//        String data = buildHashData(params);
//        String calculatedHash = hmacSHA512(hashSecret, data);
//        return calculatedHash.equalsIgnoreCase(secureHash);
//    }
//
//    private String buildQuery(Map<String, String> params) {
//        return params.entrySet().stream()
//                .sorted(Map.Entry.comparingByKey())
//                .map(e -> e.getKey() + "=" +
//                        URLEncoder.encode(e.getValue(), StandardCharsets.UTF_8))
//                .collect(Collectors.joining("&"));
//    }
//
//    private String buildHashData(Map<String, String> params) {
//        return params.entrySet().stream()
//                .sorted(Map.Entry.comparingByKey())
//                .map(e -> e.getKey() + "=" + e.getValue())
//                .collect(Collectors.joining("&"));
//    }
//
//    private String hmacSHA512(String key, String data) {
//        try {
//            Mac mac = Mac.getInstance("HmacSHA512");
//            mac.init(new SecretKeySpec(key.getBytes(), "HmacSHA512"));
//            byte[] raw = mac.doFinal(data.getBytes());
//            StringBuilder sb = new StringBuilder();
//            for (byte b : raw) sb.append(String.format("%02x", b));
//            return sb.toString();
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    private String getClientIp(HttpServletRequest request) {
//        String ip = request.getHeader("X-Forwarded-For");
//        if (ip != null && !ip.isBlank()) {
//            return ip.split(",")[0];
//        }
//        return "127.0.0.1"; // Hard fix for sandbox
//    }
}