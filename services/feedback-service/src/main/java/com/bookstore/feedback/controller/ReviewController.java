package com.bookstore.feedback.controller;

import com.bookstore.feedback.dto.AverageRatingResponse;
import com.bookstore.feedback.dto.ReviewRequest;
import com.bookstore.feedback.dto.ReviewResponse;
import com.bookstore.feedback.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/feedback")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Review Management", description = "APIs for managing product reviews and feedback")
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    @Operation(summary = "Submit a review", description = "Creates a new review for a product")
    public ResponseEntity<ReviewResponse> submitReview(
            @Parameter(description = "Product ID") @RequestParam Long productId,
            @Valid @RequestBody ReviewRequest request) {
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long userId = Long.valueOf(auth.getName());
        
        ReviewResponse review = reviewService.submitReview(productId, userId, request);
        return ResponseEntity.ok(review);
    }

    @GetMapping("/product/{id}")
    @Operation(summary = "Get product reviews", description = "Retrieves all reviews for a specific product")
    public ResponseEntity<List<ReviewResponse>> getProductReviews(
            @Parameter(description = "Product ID") @PathVariable Long id) {
        
        List<ReviewResponse> reviews = reviewService.getProductReviews(id);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/product/{id}/rating")
    @Operation(summary = "Get product average rating", description = "Calculates and returns the average rating for a product")
    public ResponseEntity<AverageRatingResponse> getProductAverageRating(
            @Parameter(description = "Product ID") @PathVariable Long id) {
        
        AverageRatingResponse rating = reviewService.getProductAverageRating(id);
        return ResponseEntity.ok(rating);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a review", description = "Updates an existing review (only by the review owner)")
    public ResponseEntity<ReviewResponse> updateReview(
            @Parameter(description = "Review ID") @PathVariable Long id,
            @Valid @RequestBody ReviewRequest request) {
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long userId = Long.valueOf(auth.getName());
        
        ReviewResponse review = reviewService.updateReview(id, userId, request);
        return ResponseEntity.ok(review);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a review", description = "Deletes a review (by owner or admin)")
    public ResponseEntity<Void> deleteReview(
            @Parameter(description = "Review ID") @PathVariable Long id) {
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long userId = Long.valueOf(auth.getName());
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
        
        if (isAdmin) {
            reviewService.deleteReviewAdmin(id);
        } else {
            reviewService.deleteReview(id, userId);
        }
        
        return ResponseEntity.ok().build();
    }
}
