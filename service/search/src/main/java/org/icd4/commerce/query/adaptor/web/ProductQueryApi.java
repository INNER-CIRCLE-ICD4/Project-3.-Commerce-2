package org.icd4.commerce.query.adaptor.web;

import lombok.RequiredArgsConstructor;
import org.icd4.commerce.query.adaptor.web.dto.ProductSearchRequest;
import org.icd4.commerce.query.adaptor.web.dto.SearchResultResponse;
import org.icd4.commerce.query.application.provided.ProductQueryService;
import org.icd4.commerce.shared.domain.Product;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// 검색 API
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/product")
public class ProductQueryApi {

    private final ProductQueryService productQueryService;

    @GetMapping("/{productId}")
    public Product findById(@PathVariable String productId) throws IOException {
        return productQueryService.findById(productId);
    }

    @GetMapping("/search")
    public List<SearchResultResponse> searchProducts(ProductSearchRequest searchRequest,
                                                     @RequestParam(defaultValue = "0", required = false) int page,
                                                     @RequestParam(defaultValue = "10", required = false) int size) throws IOException {
        return productQueryService.search(searchRequest, page, size);
    }

    @GetMapping("/search_2")
    public List<SearchResultResponse> searchProducts2(ProductSearchRequest request,
                                                      @RequestParam(defaultValue = "0", required = false) int page,
                                                      @RequestParam(defaultValue = "10", required = false) int size) throws IOException {
        return productQueryService.searchWithAdvancedOptions(request, page, size);
    }

    @GetMapping("/autocomplete")
    public List<String> getAutocompleteSuggestions(@RequestParam String prefix) throws IOException {
        return productQueryService.getAutocompleteSuggestions(prefix);
    }

    @GetMapping("/autocomplete_2")
    public List<String> getAutocompleteSuggestions2(@RequestParam String prefix) throws IOException {
        return productQueryService.getAutocompleteSuggestions2(prefix);
    }
}
