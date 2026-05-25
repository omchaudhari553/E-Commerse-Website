package com.springboot.order.kafka;

import java.time.LocalDateTime;

public class OrderEvent {
    private String eventType;
    private Long orderId;
    private Long userId;
    private String userEmail;
    private LocalDateTime timestamp;
    private String status;

    public OrderEvent() {
    }

    public OrderEvent(String eventType, Long orderId, Long userId, String userEmail, String status) {
        this.eventType = eventType;
        this.orderId = orderId;
        this.userId = userId;
        this.userEmail = userEmail;
        this.status = status;
        this.timestamp = LocalDateTime.now();
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
