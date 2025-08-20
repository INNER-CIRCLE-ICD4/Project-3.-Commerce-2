package org.icd4.commerce.query.application.provided;

import lombok.RequiredArgsConstructor;
import org.icd4.commerce.query.adaptor.web.dto.ProductSearchRequest;
import org.icd4.commerce.query.application.required.ProductRepository;
import org.icd4.commerce.query.adaptor.web.dto.SearchResultResponse;
import org.icd4.commerce.shared.domain.Product;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.StringQuery;
import org.springframework.data.elasticsearch.core.suggest.response.Suggest;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

// 사용자로부터 검색을 요청 받는 곳
// dto로 변환해서 반환해줌
@Service
@RequiredArgsConstructor
public class SearchService {

    private final ProductRepository productRepository;
    private final ElasticsearchOperations elasticsearchOperations;

    public List<SearchResultResponse> search(ProductSearchRequest options, int page, int size) throws IOException {

        List<Product> products;

        Pageable pageable = PageRequest.of(page, size, Sort.by(options.sortField(), options.sortOrder()));
        boolean isSimpleSearch = (options.filters() == null || options.filters().isEmpty());

        // 상황에 따라 메서드가 각자 다르게 동작하도록 분기 search() -> 동작 keyword()
        if (options.keyword() != null && !options.keyword().isBlank() && options.categoryId() == null && isSimpleSearch) {
            products = productRepository.findByNameOrBrandOrDescriptionMatches(options.keyword(),options.keyword(),
                    options.keyword(), pageable);
        } else if (options.categoryId() != null && !options.categoryId().isBlank() && options.keyword() == null && isSimpleSearch) {
            products = productRepository.findByCategoryId(options.categoryId(), pageable);
        } else {
            products = productRepository.searchAdvanced(
                    options.keyword(),
                    options.categoryId(),
                    BigDecimal.valueOf(options.minPrice()),
                    BigDecimal.valueOf(options.maxPrice()),
                    pageable);
        }
        return products.stream()
                .map(SearchResultResponse::of)
                .collect(Collectors.toList());
    }

    public List<String> getAutocompleteSuggestions(String prefix) throws IOException {
        String jsonQuery = String.format("""
            {
              "suggest": {
                "product-suggester": {
                  "prefix": "%s",
                  "completion": {
                    "field": "autocompleteSuggestions",
                    "size": 5
                  }
                }
              }
            }
        """, prefix); // 다섯개의 추천결과

        SearchHits<Product> searchHits = elasticsearchOperations.search(new StringQuery(jsonQuery), Product.class);

        return searchHits.getSuggest()
                .getSuggestion("product-suggester")
                .getEntries()
                .stream()
                .flatMap(entry -> entry.getOptions().stream())
                .map(Suggest.Suggestion.Entry.Option::getText)
                .collect(Collectors.toList());
    }


    /*
    public List<SearchResultDto> search(String keyword) {
        return productSearchRepository.findAllByNameAndBrandAndDescriptionAndCategoryIdMatches(keyword).stream()
                .map(product -> SearchResultDto.of(
                        product.getId(),
                        product.getSellerId(),
                        product.getName(),
                        product.getBrand(),
                        product.getBasePrice()
                ))
                .collect(Collectors.toList());
    }
    */

}
