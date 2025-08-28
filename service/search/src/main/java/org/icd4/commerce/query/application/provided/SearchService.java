package org.icd4.commerce.query.application.provided;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.CompletionSuggestOption;
import co.elastic.clients.elasticsearch.core.search.Hit;
import lombok.RequiredArgsConstructor;
import org.icd4.commerce.query.adaptor.web.dto.ProductSearchRequest;
import org.icd4.commerce.query.adaptor.web.dto.SearchResultResponse;
import org.icd4.commerce.shared.domain.Product;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SearchService {
    private final ElasticsearchClient esClient;

    public List<SearchResultResponse> search(ProductSearchRequest request, int page, int size) throws IOException {
        BoolQuery.Builder boolQueryBuilder = new BoolQuery.Builder();
        if (request.keyword() != null && !request.keyword().isEmpty()) {
            boolQueryBuilder.must(m -> m
                    .multiMatch(t -> t
                            .fields("name", "categoryId", "description")
                            .query(request.keyword())
                    )
            );
        }

        // 2. 가격 필터 (range 쿼리)
        if (request.minPrice() > 0 || request.maxPrice() > 0) {
            boolQueryBuilder.filter(f -> f.range(
                    r -> r.number(
                            n -> n.field("base_price")
                                    .gte((double) request.minPrice())
                                    .lte((double) request.maxPrice()))
            ));
        }

        // 3. 일반 필터 (brand)
        if (request.brand() != null && !request.brand().isEmpty()) {
            boolQueryBuilder.filter(f -> f.term(t -> t.field("brand").value(request.brand())));
        }

        // 4. 상품 옵션 필터 (nested 쿼리)
        if (request.options() != null && !request.options().isEmpty()) {
            BoolQuery.Builder nestedBoolQueryBuilder = new BoolQuery.Builder();

            request.options().forEach((key, values) -> {

                List<FieldValue> fieldValues = values.stream()
                        .map(FieldValue::of)
                        .collect(Collectors.toList());

                nestedBoolQueryBuilder.must(m -> m.terms(t -> t
                        .field("variants." + key)
                        .terms(terms -> terms.value(fieldValues))
                ));
            });
            boolQueryBuilder.must(m -> m
                    .nested(n -> n
                            .path("variants")
                            .query(nestedBoolQueryBuilder.build())
                    )
            );
        }

        SearchRequest.Builder requestBuilder = new SearchRequest.Builder()
                .index("product_index")
                .from(page)
                .size(size)
                .query(boolQueryBuilder.build()._toQuery());

        String sortField = request.sortField();

        if (sortField != null && !sortField.isEmpty()) {
            if ("DESC".equalsIgnoreCase(request.sortOrder())) {
                requestBuilder.sort(s -> s.field(f -> f
                        .field(sortField)
                        .order(SortOrder.Desc)));
            } else {
                requestBuilder.sort(s -> s.field(f -> f
                        .field(sortField)
                        .order(SortOrder.Asc)));
            }
        }

        SearchRequest esSearchRequest = requestBuilder.build();
        SearchResponse<Product> response = esClient.search(esSearchRequest, Product.class);

        return response.hits().hits().stream()
                .map(Hit::source)
                .filter(Objects::nonNull)
                .map(SearchResultResponse::of)
                .collect(Collectors.toList());
    }

    public List<String> getAutocompleteSuggestions(String prefix) throws IOException {
        SearchRequest request = SearchRequest.of(s -> s
                .index("product_index")
                .suggest(suggest -> suggest
                        .suggesters("product-suggester", suggester -> suggester
                                .prefix(prefix)
                                .completion(completion -> completion
                                        .field("autoCompleteSuggestions")
                                        .size(5)
                                )
                        )
                )
        );

        SearchResponse<Product> response = esClient.search(request, Product.class);

        return response.suggest()
                .get("product-suggester")
                .stream()
                .flatMap(suggestion -> suggestion.completion().options().stream())
                .map(CompletionSuggestOption::text)
                .collect(Collectors.toList());
    }
}
