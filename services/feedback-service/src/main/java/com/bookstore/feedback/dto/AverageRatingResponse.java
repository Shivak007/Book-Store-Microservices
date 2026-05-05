package com.bookstore.feedback.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Data
public class AverageRatingResponse {
    private Long productId;
    private Double averageRating;
    private Long totalReviews;
    
    public String getFormattedRating() {
        if (averageRating == null) {
            return "No reviews";
        }
        return BigDecimal.valueOf(averageRating).setScale(1, RoundingMode.HALF_UP).toString();
    }
}
