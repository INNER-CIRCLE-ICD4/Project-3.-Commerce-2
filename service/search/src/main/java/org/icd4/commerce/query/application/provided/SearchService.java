package org.icd4.commerce.query.application.provided;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.query_dsl.*;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import lombok.RequiredArgsConstructor;
import org.icd4.commerce.query.adaptor.web.dto.ProductSearchRequest;
import org.icd4.commerce.query.application.required.ProductRepository;
import org.icd4.commerce.query.adaptor.web.dto.SearchResultResponse;
import org.icd4.commerce.shared.domain.Product;
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

    private final ProductRepository productRepository;
    private final ElasticsearchOperations elasticsearchOperations;
    private final ElasticsearchClient esClient;
    private final SearchService searchService = this;

    public List<SearchResultResponse> search(ProductSearchRequest request, int page, int size) throws IOException {

        BoolQuery.Builder boolQueryBuilder = new BoolQuery.Builder();

        // 1. 키워드 검색 (match 쿼리)
        if (request.getKeyword() != null && !request.getKeyword().isEmpty()) {
            boolQueryBuilder.must(m -> m.match(t -> t.field("product_name").query(request.getKeyword())));
        }

        // 2. 가격 필터 (range 쿼리)
        if (request.getMinPrice() > 0 || request.getMaxPrice() > 0) {
            boolQueryBuilder.filter(f -> f.range(
                    r -> r.number(
                            n -> n.field("base_price")
                                    .gte((double) request.getMinPrice())
                                    .lte((double) request.getMaxPrice()))
            ));
        }

        // 3. 일반 필터 (brand_id)
        if (request.getBrand() != null && !request.getBrand().isEmpty()) {
            boolQueryBuilder.filter(f -> f.term(t -> t.field("brand").value(request.getBrand())));
        }

        // 4. 상품 옵션 필터 (nested 쿼리)
        if (request.getOptions() != null && !request.getOptions().isEmpty()) {
            BoolQuery.Builder nestedBoolQueryBuilder = new BoolQuery.Builder();

            request.getOptions().forEach((key, value) -> {
                List<String> values = (List<String>) value;

                List<FieldValue> fieldValues = values.stream()
                        .map(FieldValue::of)
                        .collect(Collectors.toList());

                nestedBoolQueryBuilder.must(m -> m.terms(t -> t
                        .field("variants." + key)
                        .terms(terms -> terms.value(fieldValues))
                ));
            });
        }

        SearchRequest esSearchRequest = new SearchRequest.Builder()
                .index("product")
                .query(boolQueryBuilder.build()._toQuery())
                .from(page)
                .build();

        SearchResponse<Product> response = esClient.search(esSearchRequest, Product.class);

        return response.hits().hits().stream()
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
