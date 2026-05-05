package com.bookstore.customerdetails.repository;
import com.bookstore.customerdetails.entity.CustomerProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface CustomerProfileRepository extends JpaRepository<CustomerProfile, Long> {
    Optional<CustomerProfile> findByUserId(Long userId);
}