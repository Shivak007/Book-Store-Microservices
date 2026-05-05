package com.bookstore.admin.repository;

import com.bookstore.admin.entity.Admin;
import com.bookstore.admin.entity.AdminRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {
    
    Optional<Admin> findByEmail(String email);
    
    boolean existsByEmail(String email);
    
    java.util.List<Admin> findByRole(AdminRole role);
}
