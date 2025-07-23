package org.icd4.commerce.domain.product;

import java.math.BigDecimal;
import java.util.List;

public record ProductCreateRequest(
        String sellerId,
        String categoryId,
        String name,
        String brand,
        String description,
        BigDecimal priceAmount,
        String priceCurrency,
        List<ProductOptionRequest> options
) {
}
