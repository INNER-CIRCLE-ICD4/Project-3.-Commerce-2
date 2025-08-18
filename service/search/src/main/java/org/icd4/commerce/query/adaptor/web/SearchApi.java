package org.icd4.commerce.query.adaptor.web;

import lombok.RequiredArgsConstructor;
import org.icd4.commerce.query.adaptor.elasticsearch.ElasticSearchProductSearcher;
import org.icd4.commerce.query.adaptor.web.dto.ProductSearch;
import org.icd4.commerce.query.adaptor.web.dto.SearchResultDto;
import org.icd4.commerce.query.application.provided.SearchService;
import org.icd4.commerce.shared.domain.Product;
import org.icd4.commerce.shared.domain.ProductSearchOptions;
import org.springframework.web.bind.annotation.*;

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
    public List<SearchResultDto> searchProducts(
            ProductSearchOptions options
            ) throws IOException {
        return searchService.search(options);
    }

    @GetMapping("/search_2")
    public List<SearchResultDto> searchProducts2(
            ProductSearch options
    ) throws IOException {
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
