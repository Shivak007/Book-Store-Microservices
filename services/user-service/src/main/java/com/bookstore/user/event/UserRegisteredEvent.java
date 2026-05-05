package com.bookstore.user.event;

import com.bookstore.user.entity.UserRole;
import java.time.LocalDateTime;

public record UserRegisteredEvent(Long userId, String email, UserRole role, LocalDateTime createdAt) {
}
