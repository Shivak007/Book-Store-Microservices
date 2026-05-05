package com.bookstore.user.dto;

import com.bookstore.user.entity.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserRegistrationRequest(
    @NotBlank(message = "Email is required")
    @Email(message = "Email format is invalid")
    String email,

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 32, message = "Password must be between 8 and 32 characters")
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$",
        message = "Password must contain uppercase, lowercase, and digit"
    )
    String password,

    UserRole role
) {
}
