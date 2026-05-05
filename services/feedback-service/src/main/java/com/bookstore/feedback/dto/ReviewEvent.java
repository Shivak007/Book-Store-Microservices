package com.bookstore.feedback.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewEvent {
    private Long reviewId;
    private Long productId;
    private Long userId;
    private String type; // REVIEW_SUBMITTED, REVIEW_UPDATED, REVIEW_DELETED
    private LocalDateTime timestamp;
}
