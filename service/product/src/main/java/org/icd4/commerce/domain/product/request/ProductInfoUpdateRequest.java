package org.icd4.commerce.domain.product.request;

public record ProductInfoUpdateRequest(
        String sellerId,
        String name,
        String brand,
        String description
) {
}
