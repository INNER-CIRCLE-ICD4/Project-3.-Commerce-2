package org.icd4.commerce.application.required;

import jakarta.persistence.EntityManager;
import org.icd4.commerce.domain.review.Review;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(
    properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1",
        "spring.datasource.username=sa",
        "spring.datasource.password="
    }
)
class ReviewRepositoryTest {
    @Autowired
    ReviewRepository reviewRepository;

    @Autowired
    EntityManager entityManager;

    @Test
    void createReview() {
        // given
        Review review = Review.create(
                "orderId",
                "productId",
                "sku",
                "userId",
                "title",
                "content",
                5
        );
        assertThat(review.getReviewId()).isNull();

        // when
        reviewRepository.save(review);

        // then
        assertThat(review.getReviewId()).isNotNull();

        entityManager.flush();
        entityManager.clear();

        Review found = reviewRepository.findByReviewId(review.getReviewId()).orElseThrow();
        assertThat(found.getOrderId()).isEqualTo("orderId");
        assertThat(found.getProductId()).isEqualTo("productId");
        assertThat(found.getSku()).isEqualTo("sku");
        assertThat(found.getUserId()).isEqualTo("userId");
        assertThat(found.getTitle()).isEqualTo("title");
        assertThat(found.getContent()).isEqualTo("content");
        assertThat(found.getRating()).isEqualTo(5);
        assertThat(found.getCreatedAt()).isNotNull();
        assertThat(found.getIsDeleted()).isFalse();
    }

    @Test
    void findByProductIdAndSku() {
        // given
        Review review1 = createAndSaveReview("productId1", "sku1", "user1");
        Review review2 = createAndSaveReview("productId1", "sku1", "user2");
        Review review3 = createAndSaveReview("productId1", "sku2", "user3");
        Review review4 = createAndSaveReview("productId2", "sku3", "user4");

        entityManager.flush();
        entityManager.clear();

        // when
        List<Review> reviews = reviewRepository.findAll("productId1", "sku1", 0L, 10L);

        // then
        assertThat(reviews).hasSize(2);
        assertThat(reviews).extracting(Review::getProductId).containsOnly("productId1");
        assertThat(reviews).extracting(Review::getSku).containsOnly("sku1");
    }

    @Test
    void count() {
        // given
        createAndSaveReview("productId1", "sku1", "user1");
        createAndSaveReview("productId1", "sku1", "user2");
        createAndSaveReview("productId1", "sku2", "user3");
        createAndSaveReview("productId2", "sku3", "user4");

        entityManager.flush();
        entityManager.clear();

        // when
        Long count = reviewRepository.count("productId1", 10L);

        // then
        assertThat(count).isEqualTo(3); // productId1에 대한 리뷰는 3개
    }

    private Review createAndSaveReview(String productId, String sku, String userId) {
        Review review = Review.create(
                "orderId",
                productId,
                sku,
                userId,
                "title",
                "content",
                5
        );
        return reviewRepository.save(review);
    }
}