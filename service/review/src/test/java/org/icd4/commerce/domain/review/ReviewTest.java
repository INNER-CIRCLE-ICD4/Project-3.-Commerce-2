package org.icd4.commerce.domain.review;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ReviewTest {
    private ReviewCreateRequest reviewCreateRequest;
    private Review review;

    @BeforeEach
    void setUp() {
        reviewCreateRequest = new ReviewCreateRequest(
                "orderId",
                "productId",
                "sku",
                "userId",
                "title",
                "content",
                5
        );
        review = Review.create(reviewCreateRequest);
    }

    @Nested
    @DisplayName("리뷰 생성 테스트")
    class CreateReviewTest {

        @Test
        @DisplayName("정상적으로 리뷰가 생성된다")
        void create() {
            assertThat(review.getOrderId()).isEqualTo("orderId");
            assertThat(review.getProductId()).isEqualTo("productId");
            assertThat(review.getSku()).isEqualTo("sku");
            assertThat(review.getUserId()).isEqualTo("userId");
            assertThat(review.getTitle()).isEqualTo("title");
            assertThat(review.getContent()).isEqualTo("content");
            assertThat(review.getRating()).isEqualTo(5);
            assertThat(review.getCreatedAt()).isNotNull();
            assertThat(review.getUpdatedAt()).isNotNull();
            assertThat(review.getIsDeleted()).isFalse();
        }

        @Test
        @DisplayName("파라미터로 직접 생성할 수 있다")
        void createWithParameters() {
            Review paramReview = Review.create(
                    "orderId",
                    "productId",
                    "sku",
                    "userId",
                    "title",
                    "content",
                    5
            );

            assertThat(paramReview.getOrderId()).isEqualTo("orderId");
            assertThat(paramReview.getProductId()).isEqualTo("productId");
            assertThat(paramReview.getSku()).isEqualTo("sku");
            assertThat(paramReview.getUserId()).isEqualTo("userId");
            assertThat(paramReview.getTitle()).isEqualTo("title");
            assertThat(paramReview.getContent()).isEqualTo("content");
            assertThat(paramReview.getRating()).isEqualTo(5);
            assertThat(paramReview.getCreatedAt()).isNotNull();
            assertThat(paramReview.getUpdatedAt()).isNotNull();
            assertThat(paramReview.getIsDeleted()).isFalse();
        }
    }

    @Nested
    @DisplayName("리뷰 삭제 테스트")
    class DeleteReviewTest {

        @Test
        @DisplayName("리뷰가 정상적으로 삭제된다")
        void delete() throws InterruptedException {
            // given
            LocalDateTime beforeDelete = review.getUpdatedAt();

            // when
            Thread.sleep(100); // 시간 차이를 주기 위해 잠시 대기
            review.delete();

            // then
            assertThat(review.getIsDeleted()).isTrue();
            assertThat(review.getUpdatedAt()).isAfter(beforeDelete);
        }

        @Test
        @DisplayName("이미 삭제된 리뷰를 삭제하면 예외가 발생한다")
        void deleteAlreadyDeletedReview() {
            // given
            review.delete();

            // when & then
            assertThatThrownBy(() -> review.delete())
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("이미 삭제된 리뷰입니다");
        }
    }
}