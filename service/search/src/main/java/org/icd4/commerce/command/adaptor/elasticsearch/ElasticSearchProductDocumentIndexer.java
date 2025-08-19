package org.icd4.commerce.command.adaptor.elasticsearch;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.icd4.commerce.command.application.required.ProductCommandRepository;
import org.icd4.commerce.command.application.required.ProductDocumentIndexer;
import org.icd4.commerce.shared.domain.Product;
import org.icd4.commerce.shared.domain.ProductCreateRequest;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ElasticSearchProductDocumentIndexer implements ProductDocumentIndexer {

    private final ElasticsearchClient esClient;
    private final ProductCommandRepository productCommandRepository;

    @Transactional
    @Override
    public String indexProduct(ProductCreateRequest request) throws IOException {
        Product index = createProduct(request);
        IndexRequest<Product> indexRequest = IndexRequest.of(i -> i
                .index("product_index")
                .id(request.productId())
                .document(index)
        );
        IndexResponse response = esClient.index(indexRequest);
        return response.id();
    }

    @Transactional
    @Override
    public void deleteProduct(String productId) {
        productCommandRepository.deleteById(productId);
    }

    private Product createProduct(ProductCreateRequest request) {
        request.autoCompleteSuggestions().addAll(generateSuggestions(request));
        return request.toProduct();
    }

    private List<String> generateSuggestions(ProductCreateRequest request) {
        List<String> suggestions = new ArrayList<>();

        if (request.name() != null) {
            suggestions.add(request.name());
            suggestions.addAll(Arrays.asList(request.name().split("\\s+")));
        }

        if (request.brand() != null) {
            suggestions.add(request.brand());
            suggestions.add(request.brand() + " " + request.name());
        }

        return suggestions.stream()
                .filter(s -> s != null && !s.trim().isEmpty())
                .distinct()
                .collect(Collectors.toList());
    }
}
