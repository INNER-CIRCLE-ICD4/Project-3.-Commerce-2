package org.icd4.commerce.domain.review;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.util.Objects;

import static java.util.Objects.requireNonNull;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reviewId;

    private String orderId;
    private String productId;
    private String sku;

    private String userId;
    private String title;
    private String content;
    private Integer rating;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean isDeleted;

    public static Review create(
            String orderId,
            String productId,
            String sku,
            String userId,
            String title,
            String content,
            Integer rating
    ) {
        Review review = new Review();
        review.orderId = orderId;
        review.productId = productId;
        review.sku = sku;
        review.userId = userId;
        review.title = title;
        review.content = content;
        review.rating = rating;
        review.createdAt = LocalDateTime.now();
        review.updatedAt = LocalDateTime.now();
        review.isDeleted = false;
        return review;
    }

    public static Review create(ReviewCreateRequest request) {
        Review review = new Review();
        review.orderId = requireNonNull(request.orderId());
        review.productId = requireNonNull(request.productId());
        review.sku = requireNonNull(request.sku());
        review.userId = request.userId();
        review.title = request.title();
        review.content = request.content();
        review.rating = request.rating();
        review.createdAt = LocalDateTime.now();
        review.updatedAt = LocalDateTime.now();
        review.isDeleted = false;
        return review;
    }

    public void delete() {
        Assert.state(this.isDeleted == false, "이미 삭제된 리뷰입니다.");
        this.isDeleted = true;
        this.updatedAt = LocalDateTime.now();
    }
}
