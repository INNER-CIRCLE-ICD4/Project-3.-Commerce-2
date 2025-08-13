package org.icd4.commerce.query.adaptor.elasticsearch;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import lombok.RequiredArgsConstructor;
import org.icd4.commerce.query.application.required.ProductSearcher;
import org.icd4.commerce.shared.domain.Product;
import org.icd4.commerce.query.adaptor.web.dto.ProductSearch;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ElasticSearchProductSearcher implements ProductSearcher {

    private final ElasticsearchClient esClient;
    private static final String INDEX_NAME = "product_index";

    @Override
    public List<Product> searchWithAdvancedOptions(ProductSearch criteria) throws IOException {
        SearchRequest request = new ElasticQueryBuilder()
                .index(INDEX_NAME)
                .keyword(criteria.keyword())
                .category(criteria.categoryId())
                .filters(criteria.filters())
                .sort(criteria.sortField(), criteria.sortOrder())
                .build();

        SearchResponse<Product> response = esClient.search(request, Product.class);
        return response.hits().hits().stream()
                .map(Hit::source)
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getAutocompleteSuggestions(String prefix) throws IOException {
        return List.of();
    }
}
