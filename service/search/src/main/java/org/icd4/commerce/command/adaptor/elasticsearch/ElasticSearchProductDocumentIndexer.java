package org.icd4.commerce.command.adaptor.elasticsearch;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import lombok.RequiredArgsConstructor;
import org.icd4.commerce.command.application.required.ProductDocumentIndexer;
import org.icd4.commerce.command.application.required.ProductRepository;
import org.icd4.commerce.shared.domain.Product;
import org.springframework.data.elasticsearch.core.suggest.Completion;
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
    private final ProductRepository productRepository;

    @Override
    public String indexProduct(Product product) throws IOException {
        Product index = createProduct(product);
        IndexRequest<Product> indexRequest = IndexRequest.of(i -> i
            .index("product_index")
            .id(product.getId())
            .document(index)
        );
        IndexResponse response = esClient.index(indexRequest);
        return response.id();
    }

    @Override
    public int deleteProduct(String productId) {
        return productRepository.deleteById(productId);
    }

    private Product createProduct(Product product) {
        List<String> suggestions = generateSuggestions(product);
        Completion completion = new Completion(suggestions.toArray(new String[0]));
        product.addCompletion(completion);
        return product;
    }

    private List<String> generateSuggestions(Product product) {
        List<String> suggestions = new ArrayList<>();

        if (product.getName() != null) {
            suggestions.add(product.getName());
            suggestions.addAll(Arrays.asList(product.getName().split("\\s+")));
        }

        if (product.getBrand() != null) {
            suggestions.add(product.getBrand());
            suggestions.add(product.getBrand() + " " + product.getName());
        }

        return suggestions.stream()
                .filter(s -> s != null && !s.trim().isEmpty())
                .distinct()
                .collect(Collectors.toList());
    }


}
