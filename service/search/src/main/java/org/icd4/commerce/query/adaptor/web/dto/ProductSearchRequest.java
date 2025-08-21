package org.icd4.commerce.query.adaptor.web.dto;

import java.util.List;
import java.util.Map;

public record ProductSearchRequest(
        String keyword,
        String categoryId,
        Integer minPrice,
        Integer maxPrice,
        String filters,
        String sortField,
        String sortOrder
) {

}
