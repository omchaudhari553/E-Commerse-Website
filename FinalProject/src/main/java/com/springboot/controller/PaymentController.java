package com.springboot.controller;

import com.razorpay.RazorpayException;
import com.springboot.dto.PaymentRequest;
import com.springboot.dto.PaymentResponse;
import com.springboot.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/payment")
@CrossOrigin(origins = "*")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/create")
    public ResponseEntity<?> createOrder(@RequestBody PaymentRequest request) {
        try {
            // Validate request
            Map<String, String> error = new HashMap<>();
            
            if (request == null) {
                error.put("error", "Request body cannot be null");
                return ResponseEntity.badRequest().body(error);
            }
            if (request.getOrderId() == null) {
                error.put("error", "Order ID is required");
                return ResponseEntity.badRequest().body(error);
            }
            if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
                error.put("error", "Valid amount is required");
                return ResponseEntity.badRequest().body(error);
            }
            if (request.getCurrency() == null || request.getCurrency().trim().isEmpty()) {
                error.put("error", "Currency is required");
                return ResponseEntity.badRequest().body(error);
            }
            if (request.getReceipt() == null || request.getReceipt().trim().isEmpty()) {
                error.put("error", "Receipt is required");
                return ResponseEntity.badRequest().body(error);
            }

            PaymentResponse response = paymentService.createOrder(request);
            return ResponseEntity.ok(response);
        } catch (RazorpayException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "An unexpected error occurred: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyPayment(@RequestBody Map<String, String> request) {
        try {
            // Validate parameters
            Map<String, String> error = new HashMap<>();
            
            String razorpayOrderId = request.get("razorpay_order_id");
            String razorpayPaymentId = request.get("razorpay_payment_id");
            String razorpaySignature = request.get("razorpay_signature");
            
            if (razorpayOrderId == null || razorpayOrderId.trim().isEmpty()) {
                error.put("error", "Razorpay Order ID is required");
                return ResponseEntity.badRequest().body(error);
            }
            if (razorpayPaymentId == null || razorpayPaymentId.trim().isEmpty()) {
                error.put("error", "Razorpay Payment ID is required");
                return ResponseEntity.badRequest().body(error);
            }
            if (razorpaySignature == null || razorpaySignature.trim().isEmpty()) {
                error.put("error", "Razorpay Signature is required");
                return ResponseEntity.badRequest().body(error);
            }

            boolean isValid = paymentService.verifyPayment(razorpayOrderId, razorpayPaymentId, razorpaySignature);
            
            Map<String, Object> response = new HashMap<>();
            if (isValid) {
                response.put("status", "success");
                response.put("message", "Payment verified successfully");
                return ResponseEntity.ok(response);
            } else {
                response.put("status", "error");
                response.put("message", "Payment verification failed");
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "An unexpected error occurred: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }
}
