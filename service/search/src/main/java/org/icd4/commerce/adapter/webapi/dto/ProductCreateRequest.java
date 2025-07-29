package org.icd4.commerce.adapter.webapi.dto;

import java.math.BigDecimal;
import java.util.List;

public record ProductCreateRequest(
        String productId,
        String sellerId,
        String categoryId,
        String name,
        String brand,
        String description
) {}
