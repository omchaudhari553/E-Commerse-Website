package com.springboot.notification.kafka;

import com.springboot.notification.entity.Notification;
import com.springboot.notification.repository.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumer {
    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumer.class);
    
    @Autowired
    private NotificationRepository notificationRepository;

    @KafkaListener(topics = "order-created", groupId = "notification-group")
    public void consumeOrderCreatedEvent(OrderEvent event) {
        logger.info("Consumed order-created event: {}", event.getOrderId());
        
        try {
            Notification notification = new Notification(
                event.getUserId(),
                event.getOrderId(),
                "Order Created",
                "Your order #" + event.getOrderId() + " has been created successfully.",
                "ORDER_CREATED",
                "UNREAD"
            );
            
            notificationRepository.save(notification);
            logger.info("Notification created for order ID: {}", event.getOrderId());
        } catch (Exception e) {
            logger.error("Error processing order-created event: {}", e.getMessage());
        }
    }

    @KafkaListener(topics = "order-cancelled", groupId = "notification-group")
    public void consumeOrderCancelledEvent(OrderEvent event) {
        logger.info("Consumed order-cancelled event: {}", event.getOrderId());
        
        try {
            Notification notification = new Notification(
                event.getUserId(),
                event.getOrderId(),
                "Order Cancelled",
                "Your order #" + event.getOrderId() + " has been cancelled.",
                "ORDER_CANCELLED",
                "UNREAD"
            );
            
            notificationRepository.save(notification);
            logger.info("Notification created for cancelled order ID: {}", event.getOrderId());
        } catch (Exception e) {
            logger.error("Error processing order-cancelled event: {}", e.getMessage());
        }
    }

    @KafkaListener(topics = "order-delivered", groupId = "notification-group")
    public void consumeOrderDeliveredEvent(OrderEvent event) {
        logger.info("Consumed order-delivered event: {}", event.getOrderId());
        
        try {
            Notification notification = new Notification(
                event.getUserId(),
                event.getOrderId(),
                "Order Delivered",
                "Your order #" + event.getOrderId() + " has been delivered successfully.",
                "ORDER_DELIVERED",
                "UNREAD"
            );
            
            notificationRepository.save(notification);
            logger.info("Notification created for delivered order ID: {}", event.getOrderId());
        } catch (Exception e) {
            logger.error("Error processing order-delivered event: {}", e.getMessage());
        }
    }
}
