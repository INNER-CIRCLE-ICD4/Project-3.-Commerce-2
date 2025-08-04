package org.icd4.commerce.application.provided;

import org.icd4.commerce.domain.review.Review;

import java.util.List;

public interface ReviewFinder {
    List<Review> findByProductIdAndSku(String productId, String sku, Long page, Long pageSize);

    Long count(String productId, Long page, Long pageSize);
}

