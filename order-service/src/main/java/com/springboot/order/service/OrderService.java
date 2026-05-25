package com.springboot.order.service;

import com.springboot.order.dto.CreateOrderDTO;
import com.springboot.order.dto.OrderItemDTO;
import com.springboot.order.entity.Order;
import com.springboot.order.entity.OrderItem;
import com.springboot.order.kafka.KafkaProducer;
import com.springboot.order.kafka.OrderEvent;
import com.springboot.order.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {
    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private KafkaProducer kafkaProducer;
    
    @Autowired
    private RestTemplate restTemplate;
    
    @Transactional
    public Order createOrder(CreateOrderDTO request, Long userId) {
        logger.info("Creating order for user: {}", request.getUserEmail());
        
        if (request.getOrderItems() == null || request.getOrderItems().isEmpty()) {
            throw new IllegalArgumentException("Order must contain at least one item");
        }
        
        Order order = new Order();
        order.setUserId(userId);
        order.setShippingAddress(request.getShippingAddress());
        order.setStatus("PENDING");
        order.setOrderDate(LocalDateTime.now());
        
        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;
        
        for (OrderItemDTO itemDTO : request.getOrderItems()) {
            // Get product details from Product Service
            String productServiceUrl = "http://product-service/api/products/" + itemDTO.getProductId();
            ProductDTO product = restTemplate.getForObject(productServiceUrl, ProductDTO.class);
            
            if (product == null) {
                throw new IllegalArgumentException("Product not found with id: " + itemDTO.getProductId());
            }
            
            if (product.getStockQuantity() < itemDTO.getQuantity()) {
                throw new IllegalArgumentException("Insufficient stock for product: " + product.getName());
            }
            
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProductId(product.getId());
            orderItem.setProductName(product.getName());
            orderItem.setPrice(product.getPrice());
            orderItem.setQuantity(itemDTO.getQuantity());
            orderItem.setImageUrl(product.getImageUrl());
            orderItems.add(orderItem);
            
            totalAmount = totalAmount.add(product.getPrice().multiply(BigDecimal.valueOf(itemDTO.getQuantity())));
            
            // Update product stock
            updateProductStock(product.getId(), product.getStockQuantity() - itemDTO.getQuantity());
        }
        
        order.setOrderItems(orderItems);
        order.setTotalAmount(totalAmount);
        
        Order savedOrder = orderRepository.save(order);
        logger.info("Order created successfully with ID: {}", savedOrder.getId());
        
        // Publish order-created event to Kafka
        OrderEvent event = new OrderEvent("ORDER_CREATED", savedOrder.getId(), userId, request.getUserEmail(), "PENDING");
        kafkaProducer.publishOrderCreatedEvent(event);
        
        return savedOrder;
    }
    
    public List<Order> getUserOrders(Long userId) {
        logger.info("Getting orders for user ID: {}", userId);
        return orderRepository.findByUserIdOrderByOrderDateDesc(userId);
    }
    
    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with id: " + id));
    }
    
    @Transactional
    public Order updateOrderStatus(Long id, String status) {
        Order order = getOrderById(id);
        String oldStatus = order.getStatus();
        order.setStatus(status.toUpperCase());
        Order updatedOrder = orderRepository.save(order);
        
        // Publish events based on status change
        if ("CANCELLED".equals(status.toUpperCase())) {
            OrderEvent event = new OrderEvent("ORDER_CANCELLED", order.getId(), order.getUserId(), "", status.toUpperCase());
            kafkaProducer.publishOrderCancelledEvent(event);
        } else if ("DELIVERED".equals(status.toUpperCase())) {
            OrderEvent event = new OrderEvent("ORDER_DELIVERED", order.getId(), order.getUserId(), "", status.toUpperCase());
            kafkaProducer.publishOrderDeliveredEvent(event);
        }
        
        logger.info("Order status updated from {} to {} for order ID: {}", oldStatus, status, id);
        return updatedOrder;
    }
    
    @Transactional
    public void cancelOrder(Long id) {
        Order order = getOrderById(id);
        if ("DELIVERED".equals(order.getStatus())) {
            throw new IllegalArgumentException("Cannot cancel a delivered order");
        }
        
        // Restore product stock
        for (OrderItem item : order.getOrderItems()) {
            String productServiceUrl = "http://product-service/api/products/" + item.getProductId();
            ProductDTO product = restTemplate.getForObject(productServiceUrl, ProductDTO.class);
            
            if (product != null) {
                updateProductStock(product.getId(), product.getStockQuantity() + item.getQuantity());
            }
        }
        
        order.setStatus("CANCELLED");
        orderRepository.save(order);
        
        // Publish order-cancelled event
        OrderEvent event = new OrderEvent("ORDER_CANCELLED", order.getId(), order.getUserId(), "", "CANCELLED");
        kafkaProducer.publishOrderCancelledEvent(event);
    }
    
    private void updateProductStock(Long productId, Integer newStock) {
        try {
            String productServiceUrl = "http://product-service/api/products/" + productId;
            restTemplate.put(productServiceUrl + "?stockQuantity=" + newStock, null);
        } catch (Exception e) {
            logger.error("Error updating product stock: {}", e.getMessage());
        }
    }
    
    // DTO for Product Service response
    public static class ProductDTO {
        private Long id;
        private String name;
        private BigDecimal price;
        private Integer stockQuantity;
        private String imageUrl;
        
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public BigDecimal getPrice() { return price; }
        public void setPrice(BigDecimal price) { this.price = price; }
        public Integer getStockQuantity() { return stockQuantity; }
        public void setStockQuantity(Integer stockQuantity) { this.stockQuantity = stockQuantity; }
        public String getImageUrl() { return imageUrl; }
        public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    }
}
