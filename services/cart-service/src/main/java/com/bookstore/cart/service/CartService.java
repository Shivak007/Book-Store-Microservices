package com.bookstore.cart.service;

import com.bookstore.cart.client.ProductClient;
import com.bookstore.cart.dto.AddCartItemRequest;
import com.bookstore.cart.dto.UpdateCartItemRequest;
import com.bookstore.cart.model.Cart;
import com.bookstore.cart.model.CartItem;
import com.bookstore.cart.repository.CartRepository;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final ProductClient productClient;

    public CartService(CartRepository cartRepository, ProductClient productClient) {
        this.cartRepository = cartRepository;
        this.productClient = productClient;
    }

    public Cart getCart() {
        return cartRepository.findById(getCurrentUserId()).orElseGet(() -> {
            Cart cart = new Cart();
            cart.setUserId(getCurrentUserId());
            cart.setItems(new ArrayList<>());
            cart.setTotalAmount(BigDecimal.ZERO);
            return cartRepository.save(cart);
        });
    }

    public Cart addItem(AddCartItemRequest request) {
        ProductClient.ProductResponse product = productClient.getProduct(request.productId());
        Cart cart = getCart();
        Optional<CartItem> existing = cart.getItems().stream()
            .filter(i -> i.getProductId().equals(request.productId()))
            .findFirst();

        if (existing.isPresent()) {
            existing.get().setQuantity(existing.get().getQuantity() + request.quantity());
            existing.get().setUnitPrice(product.price());
            existing.get().setProductTitle(product.title());
        } else {
            CartItem item = new CartItem();
            item.setProductId(product.id());
            item.setProductTitle(product.title());
            item.setQuantity(request.quantity());
            item.setUnitPrice(product.price());
            cart.getItems().add(item);
        }
        recalculate(cart);
        return cartRepository.save(cart);
    }

    public Cart updateQuantity(UpdateCartItemRequest request) {
        ProductClient.ProductResponse product = productClient.getProduct(request.productId());
        Cart cart = getCart();
        CartItem item = cart.getItems().stream()
            .filter(i -> i.getProductId().equals(request.productId()))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Product not found in cart"));
        item.setQuantity(request.quantity());
        item.setUnitPrice(product.price());
        item.setProductTitle(product.title());
        recalculate(cart);
        return cartRepository.save(cart);
    }

    public Cart removeItem(Long productId) {
        Cart cart = getCart();
        cart.getItems().removeIf(i -> i.getProductId().equals(productId));
        recalculate(cart);
        return cartRepository.save(cart);
    }

    public void clear() {
        Cart cart = getCart();
        cart.getItems().clear();
        cart.setTotalAmount(BigDecimal.ZERO);
        cartRepository.save(cart);
    }

    public BigDecimal total() {
        return getCart().getTotalAmount();
    }

    private void recalculate(Cart cart) {
        BigDecimal total = cart.getItems().stream()
            .map(i -> i.getUnitPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        cart.setTotalAmount(total);
    }

    private String getCurrentUserId() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
