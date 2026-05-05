package com.bookstore.customerdetails.repository;
import com.bookstore.customerdetails.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface AddressRepository extends JpaRepository<Address, Long> {
    List<Address> findByUserId(Long userId);
}