package org.icd4.commerce.query.adaptor.web.dto;

import lombok.Data;
import java.util.List;
import java.util.Map;

public record ProductSearchRequest(
        String keyword,
        String categoryId,
        String brand,
        Integer minPrice,
        Integer maxPrice,
        String filters,
        String sortField,
        String sortOrder
) {

}
