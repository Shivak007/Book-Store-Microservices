package com.bookstore.admin.client;

import com.bookstore.admin.dto.ServiceUnavailableException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@FeignClient(name = "order-service")
public interface OrderServiceClient {

    @GetMapping("/api/orders/all")
    @CircuitBreaker(name = "orderService", fallbackMethod = "getAllOrdersFallback")
    ResponseEntity<Map<String, Object>> getAllOrders(@RequestParam(defaultValue = "0") int page, 
                                                   @RequestParam(defaultValue = "10") int size);

    @PutMapping("/api/orders/{id}/status")
    @CircuitBreaker(name = "orderService", fallbackMethod = "updateOrderStatusFallback")
    ResponseEntity<Void> updateOrderStatus(@PathVariable("id") Long id, 
                                          @RequestParam String status);

    // Fallback methods
    default ResponseEntity<Map<String, Object>> getAllOrdersFallback(int page, int size, Exception ex) {
        throw new ServiceUnavailableException("Order service is currently unavailable");
    }

    default ResponseEntity<Void> updateOrderStatusFallback(Long id, String status, Exception ex) {
        throw new ServiceUnavailableException("Order service is currently unavailable");
    }
}
