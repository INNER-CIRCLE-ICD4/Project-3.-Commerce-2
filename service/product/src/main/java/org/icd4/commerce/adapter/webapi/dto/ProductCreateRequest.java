package org.icd4.commerce.adapter.webapi.dto;

import java.math.BigDecimal;
import java.util.List;

public record ProductCreateRequest(
        Long sellerId,
        String name,
        String brand,
        String categoryId,
        BigDecimal priceAmount,
        String priceCurrency,
        List<ProductOptionRequest> options
) {}
