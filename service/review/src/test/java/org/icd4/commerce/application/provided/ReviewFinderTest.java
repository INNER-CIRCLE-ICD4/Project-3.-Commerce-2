package org.icd4.commerce.application.provided;

import jakarta.persistence.EntityManager;
import org.icd4.commerce.domain.review.Review;
import org.icd4.commerce.domain.review.ReviewCreateRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
public class ReviewFinderTest {

    private final ReviewFinder reviewFinder;
    private final EntityManager entityManager;

    public ReviewFinderTest(ReviewFinder reviewFinder, EntityManager entityManager) {
        this.reviewFinder = reviewFinder;
        this.entityManager = entityManager;
    }

    @BeforeEach
    void setUp() {
        // Create test reviews
        createTestReview("productId1", "sku1", "userId1");
        createTestReview("productId1", "sku1", "userId2");
        createTestReview("productId1", "sku2", "userId3");
        createTestReview("productId2", "sku3", "userId4");

        entityManager.flush();
        entityManager.clear();
    }

    @Test
    @DisplayName("상품 ID와 SKU로 리뷰를 조회할 수 있다")
    void findByProductIdAndSku() {
        // when
        List<Review> reviews = reviewFinder.findByProductIdAndSku("productId1", "sku1", 1L, 10L);

        // then
        assertThat(reviews).hasSize(2);
        assertThat(reviews).allMatch(review ->
                review.getProductId().equals("productId1") && review.getSku().equals("sku1"));
    }

    @Test
    @DisplayName("상품 ID로 리뷰 수를 조회할 수 있다")
    void count() {
        // when
        Long count = reviewFinder.count("productId1", 1L, 10L);

        // then
        assertThat(count).isEqualTo(3); // productId1에 대한 리뷰는 3개
    }

    @Test
    @DisplayName("파라미터 검증이 정상적으로 동작한다")
    void validationParameter() {
        // when & then
        assertThatThrownBy(() -> reviewFinder.findByProductIdAndSku(null, "sku1", 1L, 10L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("productId cannot be null or empty");

        assertThatThrownBy(() -> reviewFinder.findByProductIdAndSku("", "sku1", 1L, 10L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("productId cannot be null or empty");

        assertThatThrownBy(() -> reviewFinder.findByProductIdAndSku("productId1", null, 1L, 10L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("sku cannot be null or empty");

        assertThatThrownBy(() -> reviewFinder.findByProductIdAndSku("productId1", "", 1L, 10L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("sku cannot be null or empty");
    }

    private Review createTestReview(String productId, String sku, String userId) {
        ReviewCreateRequest request = createTestReviewRequest(productId, sku, userId);
        var review = Review.create(request);
        entityManager.persist(review);
        entityManager.flush();
        return review;
    }

    private ReviewCreateRequest createTestReviewRequest(String productId, String sku, String userId) {
        return new ReviewCreateRequest(
                "orderId",
                productId,
                sku,
                userId,
                "title",
                "content",
                5
        );
    }
}