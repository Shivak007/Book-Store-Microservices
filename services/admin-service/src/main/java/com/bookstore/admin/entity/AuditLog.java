package com.bookstore.admin.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
public class AuditLog {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "admin_id", nullable = false)
    private Long adminId;
    
    @Column(nullable = false)
    private String action;
    
    @Column(name = "target_id")
    private Long targetId;
    
    @CreationTimestamp
    @Column(name = "timestamp", nullable = false, updatable = false)
    private LocalDateTime timestamp;
    
    // Constructors
    public AuditLog() {}
    
    public AuditLog(Long id, Long adminId, String action, Long targetId, LocalDateTime timestamp) {
        this.id = id;
        this.adminId = adminId;
        this.action = action;
        this.targetId = targetId;
        this.timestamp = timestamp;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getAdminId() { return adminId; }
    public void setAdminId(Long adminId) { this.adminId = adminId; }
    
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    
    public Long getTargetId() { return targetId; }
    public void setTargetId(Long targetId) { this.targetId = targetId; }
    
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
