package com.bookstore.customerdetails.entity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
@Entity
@Table(name = "customer_profiles")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class CustomerProfile {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false)
    private Long userId;
    private String firstName;
    private String lastName;
    private String phone;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    @PrePersist protected void onCreate() { createdAt = LocalDateTime.now(); updatedAt = LocalDateTime.now(); }
    @PreUpdate  protected void onUpdate() { updatedAt = LocalDateTime.now(); }
}