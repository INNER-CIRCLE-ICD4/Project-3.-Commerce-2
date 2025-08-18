package org.icd4.commerce.query.adaptor.web;

import lombok.RequiredArgsConstructor;
import org.icd4.commerce.query.adaptor.web.dto.SearchResultDto;
import org.icd4.commerce.query.application.provided.SearchService;
import org.icd4.commerce.query.adaptor.web.dto.ProductSearch;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

// 검색 API
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/product")
public class SearchApi {

    private final SearchService searchService;

    @GetMapping
    public List<SearchResultDto> searchProducts(ProductSearch request) throws IOException {
        return searchService.search(request);
    }

    @GetMapping("/autocomplete")
    public List<String> getAutocompleteSuggestions(@RequestParam String prefix) throws IOException {
        return searchService.getAutocompleteSuggestions(prefix);
    }
}
