package com.bookstore.wishlist.controller;

import com.bookstore.wishlist.entity.WishList;
import com.bookstore.wishlist.service.WishListService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/wishlist")
public class WishListController {

    private final WishListService wishListService;

    public WishListController(WishListService wishListService) {
        this.wishListService = wishListService;
    }

    @GetMapping
    public ResponseEntity<WishList> get() {
        return ResponseEntity.ok(wishListService.getWishList());
    }

    @PostMapping("/add/{productId}")
    public ResponseEntity<WishList> add(@PathVariable Long productId) {
        return ResponseEntity.ok(wishListService.addProduct(productId));
    }

    @DeleteMapping("/remove/{productId}")
    public ResponseEntity<WishList> remove(@PathVariable Long productId) {
        return ResponseEntity.ok(wishListService.removeProduct(productId));
    }

    @DeleteMapping("/clear")
    public ResponseEntity<Void> clear() {
        wishListService.clear();
        return ResponseEntity.noContent().build();
    }
}
