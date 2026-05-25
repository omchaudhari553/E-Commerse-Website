package com.springboot.order.controller;

import com.springboot.order.dto.CreateOrderDTO;
import com.springboot.order.entity.Order;
import com.springboot.order.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*")
public class OrderController {
    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);
    
    @Autowired
    private OrderService orderService;
    
    @Autowired
    private RestTemplate restTemplate;
    
    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody CreateOrderDTO request) {
        logger.info("Received order creation request for user: {}", request.getUserEmail());
        
        try {
            // Get user ID from User Service using email
            String userServiceUrl = "http://user-service/api/users/email/" + request.getUserEmail();
            UserDTO user = restTemplate.getForObject(userServiceUrl, UserDTO.class);
            
            if (user == null) {
                throw new IllegalArgumentException("User not found with email: " + request.getUserEmail());
            }
            
            Order order = orderService.createOrder(request, user.getId());
            return ResponseEntity.ok(order);
        } catch (IllegalArgumentException e) {
            logger.error("Error creating order: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error creating order", e);
            throw new RuntimeException("Failed to create order: " + e.getMessage());
        }
    }
    
    @GetMapping("/user/{userEmail}")
    public ResponseEntity<List<Order>> getUserOrders(@PathVariable String userEmail) {
        logger.info("Getting orders for user: {}", userEmail);
        
        try {
            // Get user ID from User Service using email
            String userServiceUrl = "http://user-service/api/users/email/" + userEmail;
            UserDTO user = restTemplate.getForObject(userServiceUrl, UserDTO.class);
            
            if (user == null) {
                throw new IllegalArgumentException("User not found with email: " + userEmail);
            }
            
            List<Order> orders = orderService.getUserOrders(user.getId());
            return ResponseEntity.ok(orders);
        } catch (IllegalArgumentException e) {
            logger.error("Error getting user orders: {}", e.getMessage());
            throw e;
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long id) {
        logger.info("Getting order by ID: {}", id);
        Order order = orderService.getOrderById(id);
        return ResponseEntity.ok(order);
    }
    
    @PutMapping("/{id}/status")
    public ResponseEntity<Order> updateOrderStatus(@PathVariable Long id, @RequestParam String status) {
        logger.info("Updating status of order {} to {}", id, status);
        Order updatedOrder = orderService.updateOrderStatus(id, status);
        return ResponseEntity.ok(updatedOrder);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> cancelOrder(@PathVariable Long id) {
        logger.info("Cancelling order with ID: {}", id);
        orderService.cancelOrder(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Order cancelled successfully");
        return ResponseEntity.ok(response);
    }
    
    // DTO for User Service response
    public static class UserDTO {
        private Long id;
        private String email;
        
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
    }
}
