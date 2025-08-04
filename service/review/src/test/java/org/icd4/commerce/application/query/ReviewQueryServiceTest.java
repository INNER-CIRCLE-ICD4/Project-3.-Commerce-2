package org.icd4.commerce.application.query;

import jakarta.persistence.EntityManager;
import org.icd4.commerce.adapter.webapi.dto.ReviewPageResponse;
import org.icd4.commerce.application.provided.ReviewRegister;
import org.icd4.commerce.domain.review.ReviewCreateRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class ReviewQueryServiceTest {

    private final ReviewQueryService reviewQueryService;
    private final ReviewRegister reviewRegister;
    private final EntityManager entityManager;

    ReviewQueryServiceTest(ReviewQueryService reviewQueryService, ReviewRegister reviewRegister, EntityManager entityManager) {
        this.reviewQueryService = reviewQueryService;
        this.reviewRegister = reviewRegister;
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
    @DisplayName("상품 ID와 SKU로 리뷰 페이지를 조회할 수 있다")
    void findAll() {
        // when
        ReviewPageResponse response = reviewQueryService.findAll("productId1", "sku1", 1L, 10L);

        // then
        assertThat(response.reviews()).hasSize(2);
        assertThat(response.reviews()).allMatch(review -> 
            review.productId().equals("productId1") && review.sku().equals("sku1"));
        assertThat(response.reviewCount()).isEqualTo(3); // productId1에 대한 리뷰는 총 3개
    }

    @Test
    @DisplayName("페이지네이션이 정상적으로 동작한다")
    void pagination() {
        // Create more reviews for pagination testing
        for (int i = 0; i < 10; i++) {
            createTestReview("productId3", "sku4", "user" + i);
        }
        entityManager.flush();
        entityManager.clear();

        // when - first page with 5 items
        ReviewPageResponse firstPage = reviewQueryService.findAll("productId3", "sku4", 1L, 5L);
        
        // then
        assertThat(firstPage.reviews()).hasSize(5);
        assertThat(firstPage.reviewCount()).isEqualTo(10);

        // when - second page with 5 items
        ReviewPageResponse secondPage = reviewQueryService.findAll("productId3", "sku4", 2L, 5L);
        
        // then
        assertThat(secondPage.reviews()).hasSize(5);
        assertThat(secondPage.reviewCount()).isEqualTo(10);
        
        // Ensure first and second page reviews are different
        assertThat(firstPage.reviews())
            .extracting(review -> review.userId())
            .doesNotContainAnyElementsOf(
                secondPage.reviews().stream().map(review -> review.userId()).toList()
            );
    }

    @Test
    @DisplayName("존재하지 않는 상품 ID로 조회하면 빈 결과가 반환된다")
    void findAllWithNonExistentProductId() {
        // when
        ReviewPageResponse response = reviewQueryService.findAll("nonExistentProductId", "sku1", 1L, 10L);

        // then
        assertThat(response.reviews()).isEmpty();
        assertThat(response.reviewCount()).isZero();
    }

    private void createTestReview(String productId, String sku, String userId) {
        ReviewCreateRequest request = new ReviewCreateRequest(
                "orderId",
                productId,
                sku,
                userId,
                "title",
                "content",
                5
        );
        reviewRegister.create(request);
    }
}