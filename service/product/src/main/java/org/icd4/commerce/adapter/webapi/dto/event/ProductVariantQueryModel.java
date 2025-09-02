package org.icd4.commerce.adapter.webapi.dto.event;

import org.icd4.commerce.domain.product.model.ProductVariant;
import org.icd4.commerce.domain.product.model.VariantStatus;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * DTO for {@link ProductVariant}
 */
public record ProductVariantQueryModel(
        String sku,
        String productId,
        String sellerId,
        List<ProductOptionQueryModel> optionCombination,
        BigDecimal price,
        VariantStatus status,
        String createdAt,
        String updatedAt
) implements Serializable {

    public static ProductVariantQueryModel fromDomain(ProductVariant variant) {
        return new ProductVariantQueryModel(
                variant.getSku(),
                variant.getProductId(),
                variant.getSellerId(),
                variant.getOptionCombinationMap().entrySet().stream()
                        .map(entry -> new ProductOptionQueryModel(entry.getValue(), entry.getKey()))
                        .toList(),
                variant.getSellingPrice().getAmount(),
                variant.getStatus(),
                variant.getCreatedAt().toString(),
                variant.getUpdatedAt().toString()
        );
    }

}