package org.icd4.commerce.query.application.provided;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.CompletionSuggestOption;
import co.elastic.clients.elasticsearch.core.search.Hit;
import lombok.RequiredArgsConstructor;
import org.icd4.commerce.query.adaptor.elasticsearch.ElasticQueryBuilder2;
import org.icd4.commerce.query.adaptor.web.dto.ProductSearchRequest;
import org.icd4.commerce.query.adaptor.web.dto.SearchResultResponse;
import org.icd4.commerce.shared.domain.Product;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SearchService {
    private final ElasticsearchClient esClient;

    public List<SearchResultResponse> search(ProductSearchRequest request, int page, int size) throws IOException {

        SearchRequest searchRequest = new ElasticQueryBuilder2("product_index")
                .keyword(request.keyword())
                .categoryId(request.categoryId())
                .brand(request.brand())
                .price(request.minPrice(), request.maxPrice())
                .filter(request.filters())
                .sort(request.sortField(),  request.sortOrder())
                .buildSearchRequest(page, size);

        SearchResponse<Product> searchResponse = esClient.search(searchRequest, Product.class);

        return searchResponse.hits().hits().stream()
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
