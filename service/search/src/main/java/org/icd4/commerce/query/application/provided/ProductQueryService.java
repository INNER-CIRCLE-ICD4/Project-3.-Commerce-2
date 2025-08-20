package org.icd4.commerce.query.application.provided;

import lombok.RequiredArgsConstructor;
import org.icd4.commerce.query.adaptor.elasticsearch.ElasticSearchProductDocumentFinder;
import org.icd4.commerce.query.adaptor.web.dto.ProductSearchOptions;
import org.icd4.commerce.query.adaptor.web.dto.ProductSearchRequest;
import org.icd4.commerce.query.adaptor.web.dto.SearchResultResponse;
import org.icd4.commerce.shared.domain.Product;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductQueryService {
    private final ElasticSearchProductDocumentFinder elasticProductDocumentFinder;
    private final ProductDocumentFinderService productDocumentFinderService;
    private final SearchService searchService;

    public Product findById(String productId) {
        return productDocumentFinderService.findById(productId);
    }

    public List<SearchResultResponse> search(ProductSearchOptions options) throws IOException {
        return searchService.search(options);
    }

    public List<SearchResultResponse> searchWithAdvancedOptions(ProductSearchRequest request, int page, int size) throws IOException {
        return elasticProductDocumentFinder.searchWithAdvancedOptions(request, page, size);
    }

    public List<String> getAutocompleteSuggestions(String prefix) throws IOException {
        return searchService.getAutocompleteSuggestions(prefix);
    }

    public List<String> getAutocompleteSuggestions2(String prefix) throws IOException {
        return elasticProductDocumentFinder.getAutocompleteSuggestions(prefix);
    }
}
