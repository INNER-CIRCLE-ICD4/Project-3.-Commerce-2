package org.icd4.commerce.domain.product;

import java.math.BigDecimal;

public record ProductInfoUpdateRequest(
        String name,
        String brand,
        String description,
        BigDecimal priceAmount,
        String priceCurrency
) {
}
