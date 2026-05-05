package com.bookstore.order.controller;

import com.bookstore.order.dto.OrderResponse;
import com.bookstore.order.entity.OrderStatus;
import com.bookstore.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Order Management", description = "APIs for managing orders")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @Operation(summary = "Place a new order", description = "Creates an order from user's cart and clears the cart")
    public ResponseEntity<OrderResponse> placeOrder() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long userId = Long.valueOf(auth.getName());
        
        OrderResponse order = orderService.placeOrder(userId);
        return ResponseEntity.ok(order);
    }

    @GetMapping
    @Operation(summary = "Get current user's orders", description = "Retrieves paginated list of orders for the authenticated user")
    public ResponseEntity<Page<OrderResponse>> getUserOrders(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size) {
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long userId = Long.valueOf(auth.getName());
        
        Pageable pageable = PageRequest.of(page, size);
        Page<OrderResponse> orders = orderService.getUserOrders(userId, pageable);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get order details", description = "Retrieves detailed information about a specific order")
    public ResponseEntity<OrderResponse> getOrder(
            @Parameter(description = "Order ID") @PathVariable Long id) {
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long userId = Long.valueOf(auth.getName());
        
        OrderResponse order = orderService.getOrderById(id, userId);
        return ResponseEntity.ok(order);
    }

    @PutMapping("/{id}/cancel")
    @Operation(summary = "Cancel an order", description = "Cancels an order if it's in PENDING status")
    public ResponseEntity<Void> cancelOrder(
            @Parameter(description = "Order ID") @PathVariable Long id) {
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long userId = Long.valueOf(auth.getName());
        
        orderService.cancelOrder(id, userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/all")
    @Operation(summary = "Get all orders (Admin only)", description = "Retrieves all orders in the system (Admin access required)")
    public ResponseEntity<Page<OrderResponse>> getAllOrders(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<OrderResponse> orders = orderService.getAllOrders(pageable);
        return ResponseEntity.ok(orders);
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "Update order status (Admin only)", description = "Updates the status of an order (Admin access required)")
    public ResponseEntity<Void> updateOrderStatus(
            @Parameter(description = "Order ID") @PathVariable Long id,
            @Parameter(description = "New order status") @RequestParam OrderStatus status) {
        
        orderService.updateOrderStatus(id, status);
        return ResponseEntity.ok().build();
    }
}
