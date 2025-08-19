package org.icd4.commerce.query.adaptor.web;

import lombok.RequiredArgsConstructor;
import org.icd4.commerce.query.adaptor.elasticsearch.ElasticSearchProductSearcher;
import org.icd4.commerce.query.adaptor.web.dto.ProductSearchOptions;
import org.icd4.commerce.query.adaptor.web.dto.ProductSearchRequest;
import org.icd4.commerce.query.adaptor.web.dto.SearchResultResponse;
import org.icd4.commerce.query.application.provided.SearchService;
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
    private final ElasticSearchProductSearcher elasticSearchProductSearcher;

    // 얼마나 많은 옵션의 데이터가 들어올지 몰라서 Post로 변경
    @GetMapping("/search")
    public List<SearchResultResponse> searchProducts(ProductSearchOptions options) throws IOException {
        return searchService.search(options);
    }

    @GetMapping("/search_2")
    public List<SearchResultResponse> searchProducts2(ProductSearchRequest options) throws IOException {
        return elasticSearchProductSearcher.searchWithAdvancedOptions(options);
    }

    @GetMapping("/autocomplete")
    public List<String> getAutocompleteSuggestions(@RequestParam String prefix) throws IOException {
        return searchService.getAutocompleteSuggestions(prefix);
    }

    @GetMapping("/autocomplete_2")
    public List<String> getAutocompleteSuggestions2(@RequestParam String prefix) throws IOException {
        return elasticSearchProductSearcher.getAutocompleteSuggestions(prefix);
    }
}
