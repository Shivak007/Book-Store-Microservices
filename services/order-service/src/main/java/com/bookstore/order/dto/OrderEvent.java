package com.bookstore.order.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderEvent {
    private Long orderId;
    private Long userId;
    private String type; // ORDER_PLACED, ORDER_SHIPPED, ORDER_DELIVERED
    private LocalDateTime timestamp;
}
