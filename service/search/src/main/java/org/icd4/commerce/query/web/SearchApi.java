package org.icd4.commerce.query.web;

import lombok.RequiredArgsConstructor;
import org.icd4.commerce.query.application.dto.SearchResultDto;
import org.icd4.commerce.query.application.SearchService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.Map;

// 검색 API
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/product")
public class SearchApi {

    private final SearchService searchService;

    @GetMapping("/search")
    public List<SearchResultDto> searchProducts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String categoryId,
            @RequestParam(required = false) Map<String, Object> filters,
            @RequestParam(required = false) String sortField,
            @RequestParam(required = false) String sortOrder
    ) throws IOException {
        return searchService.search(keyword, categoryId, filters, sortField, sortOrder);
    }

    @GetMapping("/autocomplete")
    public List<String> getAutocompleteSuggestions(@RequestParam String prefix) throws IOException {
        return searchService.getAutocompleteSuggestions(prefix);
    }
}
