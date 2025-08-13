package org.icd4.commerce.query.adaptor.web.dto;

import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

public record ProductSearch(
        @RequestParam(required = false) String keyword,
        @RequestParam(required = false) String categoryId,
        @RequestParam(required = false) Map<String, Object> filters,
        @RequestParam(required = false) String sortField,
        @RequestParam(required = false) String sortOrder
) {

}
