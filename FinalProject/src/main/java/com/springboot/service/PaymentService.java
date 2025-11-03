package com.springboot.service;

import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import com.springboot.dto.PaymentRequest;
import com.springboot.dto.PaymentResponse;
import com.springboot.repository.OrderRepository;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaymentService {
    private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);

    @Value("${razorpay.key.id}")
    private String keyId;

    @Value("${razorpay.key.secret}")
    private String keySecret;

    @Autowired
    private RazorpayClient razorpayClient;

    @Autowired
    private OrderRepository orderRepository;

    @Transactional
    public PaymentResponse createOrder(PaymentRequest request) throws RazorpayException {
        try {
            logger.info("Creating order with keyId: {}", keyId);
            // Convert amount to paise
            int amountInPaise = request.getAmount().multiply(new java.math.BigDecimal("100")).intValue();
            
            // Create order request
            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", amountInPaise);
            orderRequest.put("currency", request.getCurrency());
            orderRequest.put("receipt", request.getReceipt());
            orderRequest.put("payment_capture", 1); // Auto capture payment
            logger.info("Creating Razorpay order with request: {}", orderRequest);

            // Verify RazorpayClient initialization
            if (razorpayClient == null) {
                logger.error("RazorpayClient is null!");
                throw new RazorpayException("RazorpayClient not properly initialized");
            }

            // Create Razorpay order
            com.razorpay.Order razorpayOrder = razorpayClient.orders.create(orderRequest);
            String razorpayOrderId = razorpayOrder.get("id");
            logger.info("Created Razorpay order: {}", razorpayOrderId);

            // Update database
            com.springboot.entity.Order dbOrder = orderRepository.findById(request.getOrderId())
                    .orElseThrow(() -> new IllegalArgumentException("Order not found with id: " + request.getOrderId()));
            dbOrder.setRazorpayOrderId(razorpayOrderId);
            dbOrder.setPaymentStatus("PENDING");
            orderRepository.save(dbOrder);
            logger.info("Updated order {} with Razorpay orderId", dbOrder.getId());

            return new PaymentResponse(
                    razorpayOrderId,
                    String.valueOf(request.getAmount()),
                    keyId,
                    request.getCurrency(),
                    "Order #" + request.getOrderId()
            );
        } catch (Exception e) {
            logger.error("Error creating Razorpay order", e);
            if (e instanceof RazorpayException) {
                throw (RazorpayException) e;
            }
            throw new RazorpayException("Error creating Razorpay order: " + e.getMessage());
        }
    }

    @Transactional
    public boolean verifyPayment(String razorpayOrderId, String razorpayPaymentId, String razorpaySignature) {
        try {
            // Verify signature
            JSONObject options = new JSONObject();
            options.put("razorpay_order_id", razorpayOrderId);
            options.put("razorpay_payment_id", razorpayPaymentId);
            options.put("razorpay_signature", razorpaySignature);
            
            boolean isValidSignature = Utils.verifyPaymentSignature(options, keySecret);
            
            if (isValidSignature) {
                com.springboot.entity.Order order = orderRepository.findByRazorpayOrderId(razorpayOrderId)
                        .orElseThrow(() -> new IllegalArgumentException("Order not found with Razorpay orderId: " + razorpayOrderId));
                order.setPaymentStatus("COMPLETED");
                order.setRazorpayPaymentId(razorpayPaymentId);
                order.setRazorpaySignature(razorpaySignature);
                orderRepository.save(order);
                logger.info("Payment verified and completed for order: {}", order.getId());
                return true;
            }
            logger.warn("Invalid payment signature for Razorpay orderId: {}", razorpayOrderId);
            return false;
        } catch (Exception e) {
            logger.error("Error verifying payment", e);
            return false;
        }
    }
}
