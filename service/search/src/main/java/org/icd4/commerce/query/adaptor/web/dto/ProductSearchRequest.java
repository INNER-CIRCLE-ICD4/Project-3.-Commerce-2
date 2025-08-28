package org.icd4.commerce.query.adaptor.web.dto;

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
