package com.bookstore.feedback.service;

import com.bookstore.feedback.dto.AverageRatingResponse;
import com.bookstore.feedback.dto.ReviewEvent;
import com.bookstore.feedback.dto.ReviewRequest;
import com.bookstore.feedback.dto.ReviewResponse;
import com.bookstore.feedback.entity.Review;
import com.bookstore.feedback.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final String REVIEW_EVENTS_TOPIC = "review-events";

    @Transactional
    public ReviewResponse submitReview(Long productId, Long userId, ReviewRequest request) {
        // Check if user already reviewed this product
        if (reviewRepository.existsByProductIdAndUserId(productId, userId)) {
            throw new RuntimeException("You have already reviewed this product");
        }

        Review review = new Review();
        review.setProductId(productId);
        review.setUserId(userId);
        review.setRating(request.getRating());
        review.setComment(request.getComment());

        Review savedReview = reviewRepository.save(review);

        // Publish review event
        publishReviewEvent(savedReview.getId(), productId, userId, "REVIEW_SUBMITTED");

        log.info("Review submitted: productId={}, userId={}, rating={}", productId, userId, request.getRating());
        return convertToReviewResponse(savedReview);
    }

    public List<ReviewResponse> getProductReviews(Long productId) {
        List<Review> reviews = reviewRepository.findByProductIdOrderByCreatedAtDesc(productId);
        return reviews.stream()
                .map(this::convertToReviewResponse)
                .collect(Collectors.toList());
    }

    public AverageRatingResponse getProductAverageRating(Long productId) {
        Double averageRating = reviewRepository.findAverageRatingByProductId(productId);
        Long totalReviews = reviewRepository.countReviewsByProductId(productId);

        AverageRatingResponse response = new AverageRatingResponse();
        response.setProductId(productId);
        response.setAverageRating(averageRating);
        response.setTotalReviews(totalReviews);

        return response;
    }

    @Transactional
    public ReviewResponse updateReview(Long reviewId, Long userId, ReviewRequest request) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));

        if (!review.getUserId().equals(userId)) {
            throw new RuntimeException("You can only edit your own review");
        }

        review.setRating(request.getRating());
        review.setComment(request.getComment());

        Review updatedReview = reviewRepository.save(review);

        // Publish review event
        publishReviewEvent(reviewId, review.getProductId(), userId, "REVIEW_UPDATED");

        log.info("Review updated: reviewId={}, userId={}", reviewId, userId);
        return convertToReviewResponse(updatedReview);
    }

    @Transactional
    public void deleteReview(Long reviewId, Long userId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));

        // Check if user owns the review
        if (!review.getUserId().equals(userId)) {
            throw new RuntimeException("You can only delete your own review");
        }

        Long productId = review.getProductId();
        reviewRepository.delete(review);

        // Publish review event
        publishReviewEvent(reviewId, productId, userId, "REVIEW_DELETED");

        log.info("Review deleted: reviewId={}, userId={}", reviewId, userId);
    }

    public void deleteReviewAdmin(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));

        Long productId = review.getProductId();
        Long userId = review.getUserId();
        reviewRepository.delete(review);

        // Publish review event
        publishReviewEvent(reviewId, productId, userId, "REVIEW_DELETED");

        log.info("Review deleted by admin: reviewId={}", reviewId);
    }

    private void publishReviewEvent(Long reviewId, Long productId, Long userId, String eventType) {
        ReviewEvent event = new ReviewEvent();
        event.setReviewId(reviewId);
        event.setProductId(productId);
        event.setUserId(userId);
        event.setType(eventType);
        event.setTimestamp(LocalDateTime.now());

        kafkaTemplate.send(REVIEW_EVENTS_TOPIC, event);
        log.info("Published review event: {} for review: {}", eventType, reviewId);
    }

    private ReviewResponse convertToReviewResponse(Review review) {
        ReviewResponse response = new ReviewResponse();
        response.setId(review.getId());
        response.setProductId(review.getProductId());
        response.setUserId(review.getUserId());
        response.setRating(review.getRating());
        response.setComment(review.getComment());
        response.setCreatedAt(review.getCreatedAt());
        return response;
    }
}
