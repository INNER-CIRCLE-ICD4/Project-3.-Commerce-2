package org.icd4.commerce.application.provided;

import jakarta.persistence.EntityManager;
import org.icd4.commerce.domain.review.Review;
import org.icd4.commerce.domain.review.ReviewCreateRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class ReviewRegisterTest {

    private final ReviewRegister reviewRegister;
    private final EntityManager entityManager;
    private ReviewCreateRequest request;

    ReviewRegisterTest(ReviewRegister reviewRegister, EntityManager entityManager) {
        this.reviewRegister = reviewRegister;
        this.entityManager = entityManager;
    }

    @BeforeEach
    void setUp() {
        request = new ReviewCreateRequest(
                "orderId",
                "productId",
                "sku",
                "userId",
                "title",
                "content",
                5
        );
    }

    @Nested
    @DisplayName("리뷰 생성 테스트")
    class CreateReviewTest {

        @Test
        @DisplayName("정상적인 요청으로 리뷰가 생성되어야 한다")
        void create() {
            // when
            Review savedReview = reviewRegister.create(request);

            entityManager.flush();
            entityManager.clear();

            // then
            assertThat(savedReview.getReviewId()).isNotNull();
            assertThat(savedReview.getOrderId()).isEqualTo("orderId");
            assertThat(savedReview.getProductId()).isEqualTo("productId");
            assertThat(savedReview.getSku()).isEqualTo("sku");
            assertThat(savedReview.getUserId()).isEqualTo("userId");
            assertThat(savedReview.getTitle()).isEqualTo("title");
            assertThat(savedReview.getContent()).isEqualTo("content");
            assertThat(savedReview.getRating()).isEqualTo(5);
            assertThat(savedReview.getCreatedAt()).isNotNull();
            assertThat(savedReview.getUpdatedAt()).isNotNull();
            assertThat(savedReview.getIsDeleted()).isFalse();
        }

        @Test
        @DisplayName("필수 필드가 null이면 예외가 발생해야 한다")
        void createWithNullRequiredFields() {
            // given
            ReviewCreateRequest nullProductIdRequest = new ReviewCreateRequest(
                     "orderId", null, "sku", "userId", "title", "content", 5
            );

            ReviewCreateRequest nullSKu = new ReviewCreateRequest(
                     "orderId", "productId", null, "userId", "title", "content", 5
            );

            // when & then
            assertThatThrownBy(() -> reviewRegister.create(nullProductIdRequest))
                    .isInstanceOf(NullPointerException.class);

            assertThatThrownBy(() -> reviewRegister.create(nullSKu))
                    .isInstanceOf(NullPointerException.class);
        }
    }
}