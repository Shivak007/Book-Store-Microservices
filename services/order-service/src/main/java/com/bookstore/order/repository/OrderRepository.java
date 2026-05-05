package com.bookstore.order.repository;

import com.bookstore.order.entity.Order;
import com.bookstore.order.entity.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    Page<Order> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
    
    List<Order> findByUserId(Long userId);
    
    List<Order> findByStatus(OrderStatus status);
    
    Page<Order> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
