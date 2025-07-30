package org.icd4.commerce.domain.product.request;

import org.icd4.commerce.domain.product.model.ProductMoney;

import java.math.BigDecimal;

public record ProductVariantUpdateRequest(
        String sellerId,
        BigDecimal price,
        String currency,
        Long stockQuantity
) {
    public ProductMoney getSellingPrice() {
        return ProductMoney.of(price, currency);
    }
}
