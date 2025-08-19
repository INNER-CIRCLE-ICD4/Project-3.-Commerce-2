package org.icd4.commerce.query.application.provided;

import lombok.RequiredArgsConstructor;
import org.icd4.commerce.query.application.required.ProductQueryRepository;
import org.icd4.commerce.query.adaptor.web.dto.SearchResultResponse;
import org.icd4.commerce.shared.domain.Product;
import org.icd4.commerce.query.adaptor.web.dto.ProductSearchOptions;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.StringQuery;
import org.springframework.data.elasticsearch.core.suggest.response.Suggest;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

// 사용자로부터 검색을 요청 받는 곳
// dto로 변환해서 반환해줌
@Service
@RequiredArgsConstructor
public class SearchService {

    private final ProductQueryRepository productQueryRepository;
    private final ElasticsearchOperations elasticsearchOperations;

    public List<SearchResultResponse> search(ProductSearchOptions options) throws IOException {

        List<Product> products;

        Pageable pageable = PageRequest.of(options.page(), options.size(), Sort.by(options.sortField(), options.sortOrder()));

        boolean isSimpleSearch = (options.filters() == null || options.filters().isEmpty())
                && options.minPrice() == null && options.maxPrice() == null;

        if (options.keyword() != null && !options.keyword().isBlank() && options.categoryId() == null && isSimpleSearch) {
            // 1. 키워드만 있는 경우
            products = productQueryRepository.findByNameOrBrandOrDescriptionMatches(options.keyword(),options.keyword(),
                    options.keyword(), pageable);
        } else if (options.categoryId() != null && !options.categoryId().isBlank() && options.keyword() == null && isSimpleSearch) {
            // 2. 카테고리만 있는 경우
            products = productQueryRepository.findByCategoryId(options.categoryId(), pageable);
        } else {
            // 3. 그 외 모든 복잡한 케이스(키워드 + 필터, 키워드 + 카테고리, 정렬 등)
            products = productQueryRepository.searchAdvanced(
                    options.keyword(),
                    options.categoryId(),
                    options.minPrice(),
                    options.maxPrice(),
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
