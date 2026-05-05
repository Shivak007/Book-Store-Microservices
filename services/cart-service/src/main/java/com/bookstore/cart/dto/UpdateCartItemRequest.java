package com.bookstore.cart.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record UpdateCartItemRequest(
    @NotNull Long productId,
    @NotNull @Min(1) Integer quantity
) {
}
