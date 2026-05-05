package com.bookstore.wishlist.repository;

import com.bookstore.wishlist.entity.WishList;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WishListRepository extends JpaRepository<WishList, Long> {
    Optional<WishList> findByUserId(String userId);
}
