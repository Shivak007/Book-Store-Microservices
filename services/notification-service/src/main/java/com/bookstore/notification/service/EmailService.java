package com.bookstore.notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:noreply@bookstore.com}")
    private String fromEmail;

    @Value("${notification.email.enabled:false}")
    private boolean emailEnabled;

    public void sendOrderConfirmation(Long orderId, Long userId) {
        String subject = "Order Confirmation - Order #" + orderId;
        String message = String.format(
            "Dear Customer,\n\n" +
            "Thank you for your order! Your order #%d has been successfully placed.\n\n" +
            "Order Details:\n" +
            "- Order ID: %d\n" +
            "- Status: Confirmed\n\n" +
            "We will notify you when your order ships.\n\n" +
            "Best regards,\n" +
            "Bookstore Team",
            orderId, orderId
        );

        sendEmail("customer" + userId + "@bookstore.com", subject, message);
        log.info("Order confirmation email sent for order: {}", orderId);
    }

    public void sendShippingUpdate(Long orderId, Long userId) {
        String subject = "Order Shipped - Order #" + orderId;
        String message = String.format(
            "Dear Customer,\n\n" +
            "Great news! Your order #%d has been shipped and is on its way to you.\n\n" +
            "Shipping Details:\n" +
            "- Order ID: %d\n" +
            "- Status: Shipped\n\n" +
            "You can expect delivery within 3-5 business days.\n\n" +
            "Thank you for shopping with us!\n\n" +
            "Best regards,\n" +
            "Bookstore Team",
            orderId, orderId
        );

        sendEmail("customer" + userId + "@bookstore.com", subject, message);
        log.info("Shipping update email sent for order: {}", orderId);
    }

    public void sendDeliveryConfirmation(Long orderId, Long userId) {
        String subject = "Order Delivered - Order #" + orderId;
        String message = String.format(
            "Dear Customer,\n\n" +
            "Your order #%d has been successfully delivered!\n\n" +
            "Delivery Details:\n" +
            "- Order ID: %d\n" +
            "- Status: Delivered\n\n" +
            "We hope you enjoy your purchase. Please consider leaving a review for the products you bought.\n\n" +
            "Thank you for choosing Bookstore!\n\n" +
            "Best regards,\n" +
            "Bookstore Team",
            orderId, orderId
        );

        sendEmail("customer" + userId + "@bookstore.com", subject, message);
        log.info("Delivery confirmation email sent for order: {}", orderId);
    }

    public void sendWelcomeEmail(String email, String username) {
        String subject = "Welcome to Bookstore!";
        String message = String.format(
            "Dear %s,\n\n" +
            "Welcome to Bookstore! We're excited to have you as part of our community.\n\n" +
            "What you can do with your account:\n" +
            "- Browse and purchase books\n" +
            "- Track your orders\n" +
            "- Leave reviews for products\n" +
            "- Manage your wishlist\n\n" +
            "Thank you for joining us. Happy reading!\n\n" +
            "Best regards,\n" +
            "Bookstore Team",
            username
        );

        sendEmail(email, subject, message);
        log.info("Welcome email sent to user: {}", username);
    }

    private void sendEmail(String to, String subject, String message) {
        if (emailEnabled) {
            try {
                SimpleMailMessage mailMessage = new SimpleMailMessage();
                mailMessage.setFrom(fromEmail);
                mailMessage.setTo(to);
                mailMessage.setSubject(subject);
                mailMessage.setText(message);

                mailSender.send(mailMessage);
                log.info("Email sent successfully to: {}", to);
            } catch (Exception e) {
                log.error("Failed to send email to {}: {}", to, e.getMessage());
            }
        } else {
            // Log email to console if SMTP is not configured
            log.info("EMAIL SERVICE DISABLED - Would send email to: {}", to);
            log.info("Subject: {}", subject);
            log.info("Message: {}", message);
        }
    }
}
