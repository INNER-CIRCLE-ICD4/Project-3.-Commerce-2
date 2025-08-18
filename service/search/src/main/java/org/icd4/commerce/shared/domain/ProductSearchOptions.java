package org.icd4.commerce.shared.domain;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public record ProductSearchOptions(
        String keyword,
        String categoryId,
        BigDecimal minPrice,
        BigDecimal maxPrice,
        int page,
        int size,
        String sortField,
        String sortOrder,
        Map<String, List<String>> filters
) {

}
