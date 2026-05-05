package com.bookstore.notification.service;

import com.bookstore.notification.dto.OrderEvent;
import com.bookstore.notification.dto.UserEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final EmailService emailService;

    @KafkaListener(topics = "order-events", groupId = "notification-group")
    public void handleOrderEvent(OrderEvent event) {
        log.info("Received order event: {} for order: {} and user: {}", 
                event.getType(), event.getOrderId(), event.getUserId());

        try {
            switch (event.getType()) {
                case "ORDER_PLACED":
                    emailService.sendOrderConfirmation(event.getOrderId(), event.getUserId());
                    break;
                case "ORDER_SHIPPED":
                    emailService.sendShippingUpdate(event.getOrderId(), event.getUserId());
                    break;
                case "ORDER_DELIVERED":
                    emailService.sendDeliveryConfirmation(event.getOrderId(), event.getUserId());
                    break;
                default:
                    log.warn("Unknown order event type: {}", event.getType());
            }
        } catch (Exception e) {
            log.error("Error processing order event: {}", e.getMessage(), e);
        }
    }

    @KafkaListener(topics = "user-events", groupId = "notification-group")
    public void handleUserEvent(UserEvent event) {
        log.info("Received user event: {} for user: {} ({})", 
                event.getType(), event.getUsername(), event.getEmail());

        try {
            switch (event.getType()) {
                case "USER_REGISTERED":
                    emailService.sendWelcomeEmail(event.getEmail(), event.getUsername());
                    break;
                default:
                    log.warn("Unknown user event type: {}", event.getType());
            }
        } catch (Exception e) {
            log.error("Error processing user event: {}", e.getMessage(), e);
        }
    }
}
