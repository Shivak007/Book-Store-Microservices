package com.bookstore.admin.service;

import com.bookstore.admin.entity.Admin;
import com.bookstore.admin.entity.AdminRole;
import com.bookstore.admin.repository.AdminRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AdminService {
    
    private static final Logger log = LoggerFactory.getLogger(AdminService.class);
    
    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    
    public AdminService(AdminRepository adminRepository, PasswordEncoder passwordEncoder) {
        this.adminRepository = adminRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public Admin createAdmin(String email, String password, AdminRole role) {
        if (adminRepository.existsByEmail(email)) {
            throw new RuntimeException("Admin with email " + email + " already exists");
        }

        Admin admin = new Admin();
        admin.setEmail(email);
        admin.setPassword(passwordEncoder.encode(password));
        admin.setRole(role);

        Admin savedAdmin = adminRepository.save(admin);
        log.info("Created new admin: {} with role: {}", email, role);
        return savedAdmin;
    }

    public Admin findByEmail(String email) {
        return adminRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Admin not found with email: " + email));
    }

    public List<Admin> findAllAdmins() {
        return adminRepository.findAll();
    }

    public List<Admin> findByRole(AdminRole role) {
        return adminRepository.findByRole(role);
    }

    public boolean validatePassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}
