package org.icd4.commerce.adapter.webapi.dto.event;

import org.icd4.commerce.domain.product.model.ProductMoney;
import org.icd4.commerce.domain.product.model.ProductVariant;

import java.util.Map;

public record ProductVariantCreatedEventPayload(
        String sku,
        Map<String, String> optionCombination,
        ProductMoney sellingPrice,
        Long stockQuantity
) {
    public static ProductVariantCreatedEventPayload fromDomain(ProductVariant variant) {
        return new ProductVariantCreatedEventPayload(variant.getSku(),
                variant.getOptionCombinationMap(),
                variant.getSellingPrice(),
                variant.getStockQuantity());
    }
}
