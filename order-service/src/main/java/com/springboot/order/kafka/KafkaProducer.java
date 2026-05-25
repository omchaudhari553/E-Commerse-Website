package com.springboot.order.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducer {
    private static final Logger logger = LoggerFactory.getLogger(KafkaProducer.class);
    
    @Autowired
    private KafkaTemplate<String, OrderEvent> kafkaTemplate;

    public void publishOrderCreatedEvent(OrderEvent event) {
        try {
            kafkaTemplate.send("order-created", event);
            logger.info("Published order-created event for order ID: {}", event.getOrderId());
        } catch (Exception e) {
            logger.error("Error publishing order-created event: {}", e.getMessage());
        }
    }

    public void publishOrderCancelledEvent(OrderEvent event) {
        try {
            kafkaTemplate.send("order-cancelled", event);
            logger.info("Published order-cancelled event for order ID: {}", event.getOrderId());
        } catch (Exception e) {
            logger.error("Error publishing order-cancelled event: {}", e.getMessage());
        }
    }

    public void publishOrderDeliveredEvent(OrderEvent event) {
        try {
            kafkaTemplate.send("order-delivered", event);
            logger.info("Published order-delivered event for order ID: {}", event.getOrderId());
        } catch (Exception e) {
            logger.error("Error publishing order-delivered event: {}", e.getMessage());
        }
    }
}
