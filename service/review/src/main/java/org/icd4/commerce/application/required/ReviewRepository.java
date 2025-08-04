package org.icd4.commerce.application.required;


import org.icd4.commerce.domain.review.Review;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends Repository<Review, Long> {
    Review save(Review review);

    Optional<Review> findByReviewId(Long reviewId);

    @Query(
            value = "select review.review_id,review.order_id, review.product_id, review.sku," +
                    " review.user_id, review.title, review.content, review.rating, review.created_at, review" +
                    ".updated_at, review" +
                    ".is_deleted" +
                    " from (" +
                    "   select review_id from review where product_id = :productId and sku = :sku " +
                    "   limit :limit offset :offset " +
                    ") t left join review on t.review_id = review.review_id",
            nativeQuery = true
    )
    List<Review> findAll(
            @Param("productId") String productId,
            @Param("sku") String sku,
            @Param("offset") Long offset,
            @Param("limit") Long limit
    );

    @Query(
            value = "select count(*) " +
                    "from (select review_id " +
                    "      from review " +
                    "      where product_id = :productId " +
                    "      limit :limit) t",
            nativeQuery = true
    )
    Long count(
            @Param("productId") String productId,
            @Param("limit") Long limit
    );
}
