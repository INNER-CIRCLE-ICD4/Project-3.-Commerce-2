package org.icd4.commerce.adapter.webapi.dto;

import org.icd4.commerce.domain.product.Product;
import org.icd4.commerce.domain.product.ProductStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public record ProductResponse(
        String id,
        String sellerId,
        String name,
        String brand,
        String description,
        String categoryId,
        BigDecimal priceAmount,
        String priceCurrency,
        List<ProductOptionResponse> options,
        ProductStatus status,
        Boolean isDeleted,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime deletedAt
) {
    public static ProductResponse fromDomain(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getSellerId(),
                product.getName(),
                product.getBrand(),
                product.getDescription(),
                product.getCategoryId(),
                product.getPrice().getAmount(),
                product.getPrice().getCurrency(),
                product.getOptions().stream()
                        .map(opt -> new ProductOptionResponse(opt.getName(), opt.getDescription()))
                        .collect(Collectors.toList()),
                product.getStatus(),
                product.getIsDeleted(),
                product.getCreatedAt(),
                product.getUpdatedAt(),
                product.getDeletedAt()

        );
    }
}
