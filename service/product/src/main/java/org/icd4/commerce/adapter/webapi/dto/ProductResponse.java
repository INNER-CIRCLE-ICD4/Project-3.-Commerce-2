package org.icd4.commerce.adapter.webapi.dto;

import org.icd4.commerce.domain.product.model.Product;
import org.icd4.commerce.domain.product.model.ProductStatus;

import java.time.LocalDateTime;
import java.util.List;

public record ProductResponse(
        String id,
        String sellerId,
        String name,
        String brand,
        String description,
        String categoryId,
        ProductStatus status,
        List<ProductVariantResponse> variants,
        Boolean isDeleted,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime deletedAt
) {
    public static ProductResponse fromDomain(Product product) {
        return new ProductResponse(
                product.getSku(),
                product.getSellerId(),
                product.getName(),
                product.getBrand(),
                product.getDescription(),
                product.getCategoryId(),
                product.getStatus(),
                product.getVariants().stream()
                        .map(ProductVariantResponse::fromDomain)
                        .toList(),
                product.getIsDeleted(),
                product.getCreatedAt(),
                product.getUpdatedAt(),
                product.getDeletedAt()

        );
    }
}
