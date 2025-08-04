package org.icd4.commerce.adapter.webapi.dto;

import org.icd4.commerce.domain.review.Review;

import java.time.LocalDateTime;

public record ReviewResponse(
        Long reviewId,
        String orderId,
        String productId,
        String sku,
        String userId,
        String title,
        String content,
        Integer rating,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        Boolean isDeleted
) {
    public static ReviewResponse fromDomain(Review review) {
        return new ReviewResponse(
                review.getReviewId(),
                review.getOrderId(),
                review.getProductId(),
                review.getSku(),
                review.getUserId(),
                review.getTitle(),
                review.getContent(),
                review.getRating(),
                review.getCreatedAt(),
                review.getUpdatedAt(),
                review.getIsDeleted()
        );
    }
}
