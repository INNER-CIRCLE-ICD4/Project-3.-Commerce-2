package org.icd4.commerce.query.web;

import lombok.RequiredArgsConstructor;
import org.icd4.commerce.query.application.dto.SearchResultDto;
import org.icd4.commerce.query.application.provided.SearchService;
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

    // 얼마나 많은 옵션의 데이터가 들어올지 몰라서 Post로 변경
    @PostMapping("/search")
    public List<SearchResultDto> searchProducts(
            @RequestBody ProductSearchOptions options
            ) throws IOException {
        return searchService.search(options);
    }

    @GetMapping("/autocomplete")
    public List<String> getAutocompleteSuggestions(@RequestParam String prefix) throws IOException {
        return searchService.getAutocompleteSuggestions(prefix);
    }
}
