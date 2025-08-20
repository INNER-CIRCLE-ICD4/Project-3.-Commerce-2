package org.icd4.commerce.query.adaptor.elasticsearch;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import lombok.RequiredArgsConstructor;
import org.icd4.commerce.query.adaptor.web.dto.ProductSearchRequest;
import org.icd4.commerce.query.adaptor.web.dto.SearchResultResponse;
import org.icd4.commerce.query.application.provided.ElasticProductDocumentFinder;
import org.icd4.commerce.shared.domain.Product;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class ElasticSearchProductDocumentFinder implements ElasticProductDocumentFinder {

    private final ElasticsearchClient esClient;
    private static final String INDEX_NAME = "product_index";

    @Override
    public List<SearchResultResponse> searchWithAdvancedOptions(ProductSearchRequest criteria, int page, int size) throws IOException {
        SearchRequest request = new ElasticQueryBuilder()
                .index(INDEX_NAME)
                .keyword(criteria.keyword())
                .category(criteria.categoryId())
                .filters(criteria.filters())
                .sort(criteria.sortField(), criteria.sortOrder())
                .page(page, size)
                .build();

        SearchResponse<Product> response = esClient.search(request, Product.class);
        return response.hits().hits().stream()
                .map(Hit::source)
                .map(SearchResultResponse::of)
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getAutocompleteSuggestions(String prefix) throws IOException {
        if (invalidPrefix(prefix)) return List.of();

        SearchRequest request = getSuggestQuery(prefix);
        SearchResponse<Product> response = esClient.search(request, Product.class);

        return getCollectedResponse(response);

    }

    private boolean invalidPrefix(String prefix) {
        return (prefix == null || prefix.trim().isEmpty());
    }

    private SearchRequest getSuggestQuery(String prefix) {
        return SearchRequest.of(s -> s
                .index(INDEX_NAME)
                .query(q -> q
                        .bool(b -> b
                                .should(sh -> sh
                                        .matchPhrasePrefix(mp -> mp
                                                        .field("name")
                                                        .query(prefix)
                                                // .boost(1.5f) 가중치
                                        )
                                )
                                .should(sh -> sh
                                        .matchPhrasePrefix(mp -> mp
                                                .field("brand")
                                                .query(prefix)
                                        )
                                )
                        )
                )
                .size(10)
                .source(source -> source
                        .filter(f -> f.includes("name", "brand"))
                )
        );
    }

    private List<String> getCollectedResponse(SearchResponse<Product> response) {
        return response.hits().hits().stream()
                .map(Hit::source)
                .filter(Objects::nonNull)
                .flatMap(product -> Stream.of(product.getName(), product.getBrand())
                        .filter(Objects::nonNull))
                .distinct()
                .collect(Collectors.toList());
    }
}
