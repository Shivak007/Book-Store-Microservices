package com.bookstore.admin.client;

import com.bookstore.admin.dto.ServiceUnavailableException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@FeignClient(name = "product-service")
public interface ProductServiceClient {

    @GetMapping("/api/products/{id}")
    @CircuitBreaker(name = "productService", fallbackMethod = "getProductFallback")
    ResponseEntity<Map<String, Object>> getProduct(@PathVariable("id") Long id);

    @PutMapping("/api/products/{id}")
    @CircuitBreaker(name = "productService", fallbackMethod = "updateProductFallback")
    ResponseEntity<Map<String, Object>> updateProduct(@PathVariable("id") Long id, @RequestBody Map<String, Object> product);

    @DeleteMapping("/api/products/{id}")
    @CircuitBreaker(name = "productService", fallbackMethod = "deleteProductFallback")
    ResponseEntity<Void> deleteProduct(@PathVariable("id") Long id);

    // Fallback methods
    default ResponseEntity<Map<String, Object>> getProductFallback(Long id, Exception ex) {
        throw new ServiceUnavailableException("Product service is currently unavailable");
    }

    default ResponseEntity<Map<String, Object>> updateProductFallback(Long id, Map<String, Object> product, Exception ex) {
        throw new ServiceUnavailableException("Product service is currently unavailable");
    }

    default ResponseEntity<Void> deleteProductFallback(Long id, Exception ex) {
        throw new ServiceUnavailableException("Product service is currently unavailable");
    }
}
