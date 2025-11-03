package com.springboot.controller;

import com.springboot.entity.Order;
import com.springboot.entity.User;
import com.springboot.service.OrderService;
import com.springboot.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*")
public class OrderController {
    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);
    
    @Autowired
    private OrderService orderService;
    
    @Autowired
    private UserService userService;
    
    @GetMapping("/test")
    public ResponseEntity<String> test() {
        logger.info("Test endpoint called");
        return ResponseEntity.ok("OrderController is working!");
    }
    
    static class CreateOrderRequest {
        private Order order;
        private String userEmail;
        
        public Order getOrder() {
            return order;
        }
        
        public void setOrder(Order order) {
            this.order = order;
        }
        
        public String getUserEmail() {
            return userEmail;
        }
        
        public void setUserEmail(String userEmail) {
            this.userEmail = userEmail;
        }
    }
    
    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody CreateOrderRequest request) {
        logger.info("Received order creation request for user: {}", request.getUserEmail());
        
        if (request == null) {
            logger.error("Request body is null");
            throw new IllegalArgumentException("Request cannot be null");
        }
        if (request.getOrder() == null) {
            logger.error("Order is null");
            throw new IllegalArgumentException("Order cannot be null");
        }
        if (request.getUserEmail() == null || request.getUserEmail().trim().isEmpty()) {
            logger.error("User email is missing");
            throw new IllegalArgumentException("User email is required");
        }
        if (request.getOrder().getOrderItems() == null || request.getOrder().getOrderItems().isEmpty()) {
            logger.error("Order items are missing");
            throw new IllegalArgumentException("Order must contain at least one item");
        }
        
        logger.info("Finding user with email: {}", request.getUserEmail());
        User user = userService.getUserByEmail(request.getUserEmail());
        
        if (user == null) {
            logger.error("User not found with email: {}", request.getUserEmail());
            throw new IllegalArgumentException("User not found with email: " + request.getUserEmail());
        }
        
        logger.info("User found: {}", user.getName());
        request.getOrder().setUser(user);
        
        Order createdOrder = orderService.createOrder(request.getOrder(), user);
        logger.info("Order created successfully with ID: {}", createdOrder.getId());
        
        return ResponseEntity.ok(createdOrder);
    }
    
    @GetMapping("/user/{userEmail}")
    public ResponseEntity<List<Order>> getUserOrders(@PathVariable String userEmail) {
        logger.info("Getting orders for user: {}", userEmail);
        User user = userService.getUserByEmail(userEmail);
        
        if (user == null) {
            logger.error("User not found with email: {}", userEmail);
            throw new IllegalArgumentException("User not found with email: " + userEmail);
        }
        
        List<Order> orders = orderService.getUserOrders(user);
        logger.info("Found {} orders for user: {}", orders.size(), userEmail);
        return ResponseEntity.ok(orders);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long id) {
        logger.info("Getting order with ID: {}", id);
        Order order = orderService.getOrderById(id);
        
        if (order == null) {
            logger.error("Order not found with id: {}", id);
            throw new IllegalArgumentException("Order not found with id: " + id);
        }
        
        logger.info("Found order with ID: {}", id);
        return ResponseEntity.ok(order);
    }
    
    @PutMapping("/{id}/status")
    public ResponseEntity<Order> updateOrderStatus(@PathVariable Long id, @RequestParam String status) {
        logger.info("Updating order status for ID: {} to {}", id, status);
        if (status == null || status.trim().isEmpty()) {
            logger.error("Status cannot be empty");
            throw new IllegalArgumentException("Status cannot be empty");
        }
        
        // Verify order exists before updating
        Order order = orderService.getOrderById(id);
        
        if (order == null) {
            logger.error("Order not found with id: {}", id);
            throw new IllegalArgumentException("Order not found with id: " + id);
        }
        
        Order updatedOrder = orderService.updateOrderStatus(id, status);
        logger.info("Order status updated successfully for ID: {}", id);
        
        return ResponseEntity.ok(updatedOrder);
    }
}
