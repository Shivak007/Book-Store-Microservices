package com.bookstore.admin.client;

import com.bookstore.admin.dto.ServiceUnavailableException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@FeignClient(name = "user-service")
public interface UserServiceClient {

    @GetMapping("/api/users/all")
    @CircuitBreaker(name = "userService", fallbackMethod = "getAllUsersFallback")
    ResponseEntity<Map<String, Object>> getAllUsers(@RequestParam(defaultValue = "0") int page, 
                                                  @RequestParam(defaultValue = "10") int size);

    // Fallback methods
    default ResponseEntity<Map<String, Object>> getAllUsersFallback(int page, int size, Exception ex) {
        throw new ServiceUnavailableException("User service is currently unavailable");
    }
}
