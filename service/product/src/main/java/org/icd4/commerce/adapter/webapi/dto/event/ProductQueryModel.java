package org.icd4.commerce.adapter.webapi.dto.event;

import org.icd4.commerce.domain.product.model.Product;
import org.icd4.commerce.domain.product.model.ProductStatus;

import java.math.BigDecimal;
import java.util.List;

public record ProductQueryModel(
        String productId,
        String sellerId,
        String name,
        String brand,
        BigDecimal basePrice,
        String description,
        String categoryId,
        ProductStatus status,
        List<ProductVariantQueryModel> variants,
        Boolean isDeleted,
        String createdAt,
        String updatedAt
) {
    public static ProductQueryModel fromDomain(Product product) {
        return new ProductQueryModel(
                product.getId(),
                product.getSellerId(),
                product.getName(),
                product.getBrand(),
                product.getBasePrice().getAmount(),
                product.getDescription(),
                product.getCategoryId(),
                product.getStatus(),
                product.getVariants().stream()
                        .map(ProductVariantQueryModel::fromDomain)
                        .toList(),
                product.getIsDeleted(),
                product.getCreatedAt().toString(),
                product.getUpdatedAt().toString()

        );
    }
}
