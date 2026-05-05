package com.bookstore.order.service;

import com.bookstore.order.client.CartClient;
import com.bookstore.order.client.ProductClient;
import com.bookstore.order.dto.OrderEvent;
import com.bookstore.order.dto.OrderResponse;
import com.bookstore.order.dto.OrderItemResponse;
import com.bookstore.order.entity.*;
import com.bookstore.order.repository.OrderRepository;
import com.bookstore.order.repository.OrderItemRepository;
import com.bookstore.order.repository.OrderStatusHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderStatusHistoryRepository statusHistoryRepository;
    private final CartClient cartClient;
    private final ProductClient productClient;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final String ORDER_EVENTS_TOPIC = "order-events";

    @Transactional
    public OrderResponse placeOrder(Long userId) {
        try {
            // Get cart items
            Map<String, Object> cartResponse = cartClient.getCart(userId.toString());
            List<Map<String, Object>> cartItems = (List<Map<String, Object>>) cartResponse.get("items");
            
            if (cartItems == null || cartItems.isEmpty()) {
                throw new RuntimeException("Cart is empty");
            }

            // Create order
            Order order = new Order();
            order.setUserId(userId);
            order.setStatus(OrderStatus.PENDING);
            order.setTotalAmount(BigDecimal.ZERO);

            // Process cart items
            BigDecimal totalAmount = BigDecimal.ZERO;
            for (Map<String, Object> cartItem : cartItems) {
                Long productId = Long.valueOf(cartItem.get("productId").toString());
                Integer quantity = (Integer) cartItem.get("quantity");
                
                // Get product details
                Map<String, Object> product = productClient.getProductById(productId);
                String productTitle = (String) product.get("title");
                BigDecimal unitPrice = new BigDecimal(product.get("price").toString());
                
                // Create order item
                OrderItem orderItem = new OrderItem();
                orderItem.setProductId(productId);
                orderItem.setProductTitle(productTitle);
                orderItem.setQuantity(quantity);
                orderItem.setUnitPrice(unitPrice);
                
                order.addOrderItem(orderItem);
                totalAmount = totalAmount.add(unitPrice.multiply(BigDecimal.valueOf(quantity)));
            }
            
            order.setTotalAmount(totalAmount);
            
            // Save order
            order = orderRepository.save(order);
            
            // Add initial status history
            OrderStatusHistory history = new OrderStatusHistory();
            history.setOrder(order);
            history.setStatus(OrderStatus.PENDING);
            order.addStatusHistory(history);
            
            // Save complete order with items and history
            orderRepository.save(order);
            
            // Clear cart
            cartClient.clearCart(userId.toString());
            
            // Publish order event
            publishOrderEvent(order.getId(), userId, "ORDER_PLACED");
            
            log.info("Order placed successfully: {}", order.getId());
            return convertToOrderResponse(order);
            
        } catch (Exception e) {
            log.error("Error placing order for user {}: {}", userId, e.getMessage());
            throw new RuntimeException("Failed to place order: " + e.getMessage());
        }
    }

    public Page<OrderResponse> getUserOrders(Long userId, Pageable pageable) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable)
                .map(this::convertToOrderResponse);
    }

    public OrderResponse getOrderById(Long orderId, Long userId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        
        // Check if user owns the order or is admin
        if (!order.getUserId().equals(userId)) {
            throw new RuntimeException("Access denied");
        }
        
        return convertToOrderResponse(order);
    }

    @Transactional
    public void cancelOrder(Long orderId, Long userId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        
        if (!order.getUserId().equals(userId)) {
            throw new RuntimeException("Access denied");
        }
        
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new RuntimeException("Order cannot be cancelled in current status");
        }
        
        updateOrderStatus(order.getId(), OrderStatus.CANCELLED);
        publishOrderEvent(orderId, userId, "ORDER_CANCELLED");
        
        log.info("Order cancelled: {}", orderId);
    }

    public Page<OrderResponse> getAllOrders(Pageable pageable) {
        return orderRepository.findAllByOrderByCreatedAtDesc(pageable)
                .map(this::convertToOrderResponse);
    }

    @Transactional
    public void updateOrderStatus(Long orderId, OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        
        OrderStatus oldStatus = order.getStatus();
        order.setStatus(newStatus);
        
        // Add status history
        OrderStatusHistory history = new OrderStatusHistory();
        history.setOrder(order);
        history.setStatus(newStatus);
        order.addStatusHistory(history);
        
        orderRepository.save(order);
        
        // Publish events for specific status changes
        if (newStatus == OrderStatus.SHIPPED) {
            publishOrderEvent(orderId, order.getUserId(), "ORDER_SHIPPED");
        } else if (newStatus == OrderStatus.DELIVERED) {
            publishOrderEvent(orderId, order.getUserId(), "ORDER_DELIVERED");
        }
        
        log.info("Order {} status updated from {} to {}", orderId, oldStatus, newStatus);
    }

    private void publishOrderEvent(Long orderId, Long userId, String eventType) {
        OrderEvent event = new OrderEvent();
        event.setOrderId(orderId);
        event.setUserId(userId);
        event.setType(eventType);
        event.setTimestamp(LocalDateTime.now());
        
        kafkaTemplate.send(ORDER_EVENTS_TOPIC, event);
        log.info("Published order event: {} for order: {}", eventType, orderId);
    }

    private OrderResponse convertToOrderResponse(Order order) {
        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setUserId(order.getUserId());
        response.setStatus(order.getStatus());
        response.setTotalAmount(order.getTotalAmount());
        response.setCreatedAt(order.getCreatedAt());
        
        List<OrderItemResponse> itemResponses = order.getOrderItems().stream()
                .map(this::convertToOrderItemResponse)
                .collect(Collectors.toList());
        response.setOrderItems(itemResponses);
        
        return response;
    }

    private OrderItemResponse convertToOrderItemResponse(OrderItem item) {
        OrderItemResponse response = new OrderItemResponse();
        response.setId(item.getId());
        response.setProductId(item.getProductId());
        response.setProductTitle(item.getProductTitle());
        response.setQuantity(item.getQuantity());
        response.setUnitPrice(item.getUnitPrice());
        response.setTotalPrice(item.getTotalPrice());
        return response;
    }
}
