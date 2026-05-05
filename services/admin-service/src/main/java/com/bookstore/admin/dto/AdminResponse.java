package com.bookstore.admin.dto;

import com.bookstore.admin.entity.AdminRole;
import java.time.LocalDateTime;

public class AdminResponse {
    private Long id;
    private String email;
    private AdminRole role;
    private LocalDateTime createdAt;
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public AdminRole getRole() { return role; }
    public void setRole(AdminRole role) { this.role = role; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
