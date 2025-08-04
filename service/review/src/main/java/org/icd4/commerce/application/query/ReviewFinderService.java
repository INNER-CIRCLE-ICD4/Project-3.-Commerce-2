package org.icd4.commerce.application.query;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.icd4.commerce.application.provided.ReviewFinder;
import org.icd4.commerce.application.required.ReviewRepository;
import org.icd4.commerce.domain.review.Review;
import org.icd4.commerce.domain.shared.PageCalculator;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ReviewFinderService implements ReviewFinder {
    private final ReviewRepository reviewRepository;

    @Override
    public List<Review> findByProductIdAndSku(String productId, String sku, Long page, Long pageSize) {
        validationParameter(productId, sku);
        return reviewRepository.findAll(productId, sku, (page - 1) * pageSize, pageSize);
    }

    @Override
    public Long count(String productId, Long page, Long pageSize) {
        return reviewRepository.count(productId, PageCalculator.calculatePageLimit(page, pageSize, 10L));
    }

    private Review internalFindById(Long reviewId) {
        return reviewRepository.findByReviewId(reviewId)
                .orElseThrow(() -> new EntityNotFoundException("리뷰를 찾을 수 없습니다. reviewId::" + reviewId));
    }

    private void validationParameter(String productId, String sku) {
        if (productId == null || productId.trim().isEmpty()) {
            throw new IllegalArgumentException("productId cannot be null or empty");
        }
        if (sku == null || sku.trim().isEmpty()) {
            throw new IllegalArgumentException("sku cannot be null or empty");
        }
    }
}
