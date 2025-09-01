package org.icd4.commerce.shared.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record ProductCreateRequest(
        String productId,
        String sellerId,
        String name,
        String brand,
        String description,
        BigDecimal basePrice,
        String categoryId,
        String status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        Boolean isDeleted,
        List<String> productAttributes,
        List<String> autoCompleteSuggestions,
        List<ProductVariantDto> variants
) {
    public record ProductVariantDto(
            String sku,
            Long price,
            String status,
            List<ProductOptionDto> optionCombination
    ) {
    }

    public record ProductOptionDto(
            String name,
            String value
    ) {
    }
}