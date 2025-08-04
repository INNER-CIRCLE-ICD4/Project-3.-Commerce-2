package org.icd4.commerce.domain.review;

import jakarta.validation.constraints.NotNull;

import java.io.Serializable;

/**
 * DTO for {@link Review}
 */
public record ReviewCreateRequest(
        String orderId,
        @NotNull String productId,
        @NotNull String sku,
        String userId,
        String title,
        String content,
        Integer rating
) implements Serializable {
}