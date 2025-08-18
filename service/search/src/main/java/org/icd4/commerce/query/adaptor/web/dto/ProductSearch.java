package org.icd4.commerce.query.adaptor.web.dto;

import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

public record ProductSearch(
        String keyword,
        String categoryId,
        Map<String, Object> filters,
        String sortField,
        String sortOrder
) {

}
