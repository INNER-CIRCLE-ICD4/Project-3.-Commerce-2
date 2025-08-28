package org.icd4.commerce.adapter.webapi.dto.event;

import org.icd4.commerce.domain.product.model.Product;

import java.util.List;

public record ProductCreatedEventPayload(
        String productId,
        String sellerId,
        String name,
        String brand,
        String categoryId,
        List<ProductVariantCreatedEventPayload> variants
) {
    public static ProductCreatedEventPayload from(
            Product product) {
        return new ProductCreatedEventPayload(
                product.getSku(),
                product.getSellerId(),
                product.getName(),
                product.getBrand(),
                product.getCategoryId(),
                product.getAllVariants().stream()
                        .map(ProductVariantCreatedEventPayload::fromDomain)
                        .toList()
        );
    }
}
