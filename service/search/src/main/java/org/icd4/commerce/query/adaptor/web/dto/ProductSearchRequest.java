package org.icd4.commerce.query.adaptor.web.dto;

import java.util.Map;

public record ProductSearchRequest(
        String keyword,
        String categoryId,
        Map<String, Object> filters,
        String sortField,
        String sortOrder
) {

}
