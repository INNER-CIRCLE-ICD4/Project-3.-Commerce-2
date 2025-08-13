package org.icd4.commerce.query.application.provided;

import lombok.RequiredArgsConstructor;
import org.icd4.commerce.query.adaptor.web.dto.SearchResultDto;
import org.icd4.commerce.query.application.required.ProductSearcher;
import org.icd4.commerce.shared.domain.Product;
import org.icd4.commerce.query.adaptor.web.dto.ProductSearch;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SearchService {
    private final ProductSearcher productSearcher;

    public List<SearchResultDto> search(ProductSearch criteria) throws IOException {
        List<Product> products = productSearcher.searchWithAdvancedOptions(criteria);
        return products.stream()
                .map(SearchResultDto::of)
                .collect(Collectors.toList());
    }

    public List<String> getAutocompleteSuggestions(String prefix) throws IOException {
        return productSearcher.getAutocompleteSuggestions(prefix);
    }
}
