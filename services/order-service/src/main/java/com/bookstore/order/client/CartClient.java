package com.bookstore.order.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.Map;

@FeignClient(name = "cart-service")
public interface CartClient {
    
    @GetMapping("/api/cart")
    Map<String, Object> getCart(@RequestHeader("X-User-Id") String userId);
    
    @DeleteMapping("/api/cart")
    void clearCart(@RequestHeader("X-User-Id") String userId);
}
