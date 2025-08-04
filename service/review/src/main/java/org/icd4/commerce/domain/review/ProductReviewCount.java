package org.icd4.commerce.domain.review;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class ProductReviewCount {
    @Id
    private String productId;
    private Long reviewCount;

    public static ProductReviewCount create(String productId, Long reviewCount) {
        ProductReviewCount productReviewCount = new ProductReviewCount();
        productReviewCount.productId = productId;
        productReviewCount.reviewCount = reviewCount;
        return productReviewCount;
    }
}
