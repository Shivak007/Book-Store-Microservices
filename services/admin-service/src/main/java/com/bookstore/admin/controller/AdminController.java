package com.bookstore.admin.controller;

import com.bookstore.admin.client.OrderServiceClient;
import com.bookstore.admin.client.ProductServiceClient;
import com.bookstore.admin.client.UserServiceClient;
import com.bookstore.admin.dto.AdminRegistrationRequest;
import com.bookstore.admin.dto.AdminResponse;
import com.bookstore.admin.entity.Admin;
import com.bookstore.admin.entity.AdminRole;
import com.bookstore.admin.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@Tag(name = "Admin Management", description = "APIs for admin operations")
public class AdminController {

    private final AdminService adminService;
    private final UserServiceClient userServiceClient;
    private final ProductServiceClient productServiceClient;
    private final OrderServiceClient orderServiceClient;
    
    public AdminController(AdminService adminService, UserServiceClient userServiceClient, 
                         ProductServiceClient productServiceClient, OrderServiceClient orderServiceClient) {
        this.adminService = adminService;
        this.userServiceClient = userServiceClient;
        this.productServiceClient = productServiceClient;
        this.orderServiceClient = orderServiceClient;
    }

    @PostMapping("/register")
    @Operation(summary = "Register new admin", description = "Creates a new admin account (SUPER_ADMIN only)")
    public ResponseEntity<AdminResponse> registerAdmin(@Valid @RequestBody AdminRegistrationRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentRole = auth.getAuthorities().iterator().next().getAuthority();
        
        if (!currentRole.equals("ROLE_SUPER_ADMIN")) {
            throw new RuntimeException("Only SUPER_ADMIN can create new admins");
        }

        AdminRole role = AdminRole.valueOf(request.getRole());
        Admin admin = adminService.createAdmin(request.getEmail(), request.getPassword(), role);
        
        AdminResponse response = new AdminResponse();
        response.setId(admin.getId());
        response.setEmail(admin.getEmail());
        response.setRole(admin.getRole());
        response.setCreatedAt(admin.getCreatedAt());
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/all-users")
    @Operation(summary = "Get all users", description = "Retrieves all users via user-service")
    public ResponseEntity<Map<String, Object>> getAllUsers(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size) {
        
        return userServiceClient.getAllUsers(page, size);
    }

    @PutMapping("/products/{id}")
    @Operation(summary = "Update product", description = "Updates a product via product-service")
    public ResponseEntity<Map<String, Object>> updateProduct(
            @Parameter(description = "Product ID") @PathVariable Long id,
            @RequestBody Map<String, Object> product) {
        
        return productServiceClient.updateProduct(id, product);
    }

    @DeleteMapping("/products/{id}")
    @Operation(summary = "Delete product", description = "Deletes a product via product-service")
    public ResponseEntity<Void> deleteProduct(
            @Parameter(description = "Product ID") @PathVariable Long id) {
        
        return productServiceClient.deleteProduct(id);
    }

    @GetMapping("/orders")
    @Operation(summary = "Get all orders", description = "Retrieves all orders via order-service")
    public ResponseEntity<Map<String, Object>> getAllOrders(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size) {
        
        return orderServiceClient.getAllOrders(page, size);
    }

    @PutMapping("/orders/{id}/status")
    @Operation(summary = "Update order status", description = "Updates order status via order-service")
    public ResponseEntity<Void> updateOrderStatus(
            @Parameter(description = "Order ID") @PathVariable Long id,
            @Parameter(description = "New status") @RequestParam String status) {
        
        return orderServiceClient.updateOrderStatus(id, status);
    }
}
