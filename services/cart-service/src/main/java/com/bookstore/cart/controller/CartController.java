package com.bookstore.cart.controller;

import com.bookstore.cart.dto.AddCartItemRequest;
import com.bookstore.cart.dto.UpdateCartItemRequest;
import com.bookstore.cart.model.Cart;
import com.bookstore.cart.service.CartService;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    public ResponseEntity<Cart> getCart() {
        return ResponseEntity.ok(cartService.getCart());
    }

    @PostMapping("/add")
    public ResponseEntity<Cart> add(@Valid @RequestBody AddCartItemRequest request) {
        return ResponseEntity.ok(cartService.addItem(request));
    }

    @PutMapping("/quantity")
    public ResponseEntity<Cart> updateQuantity(@Valid @RequestBody UpdateCartItemRequest request) {
        return ResponseEntity.ok(cartService.updateQuantity(request));
    }

    @DeleteMapping("/item/{productId}")
    public ResponseEntity<Cart> remove(@PathVariable Long productId) {
        return ResponseEntity.ok(cartService.removeItem(productId));
    }

    @DeleteMapping("/clear")
    public ResponseEntity<Void> clear() {
        cartService.clear();
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/total")
    public ResponseEntity<Map<String, BigDecimal>> total() {
        return ResponseEntity.ok(Map.of("totalAmount", cartService.total()));
    }
}
