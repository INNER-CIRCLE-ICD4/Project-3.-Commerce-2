package org.icd4.commerce.adapter.webapi.dto;

import org.icd4.commerce.domain.product.model.ProductVariant;
import org.icd4.commerce.domain.product.model.VariantStatus;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for {@link ProductVariant}
 */
public record ProductVariantResponse(
        String sku,
        String productId,
        String sellerId,
        String optionCombination,
        BigDecimal price,
        VariantStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) implements Serializable {

    public static ProductVariantResponse fromDomain(ProductVariant variant) {
        return new ProductVariantResponse(
                variant.getSku(),
                variant.getProductId(),
                variant.getSellerId(),
                variant.getOptionCombination(),
                variant.getSellingPrice().getAmount(),
                variant.getStatus(),
                variant.getCreatedAt(),
                variant.getUpdatedAt()
        );
    }

}