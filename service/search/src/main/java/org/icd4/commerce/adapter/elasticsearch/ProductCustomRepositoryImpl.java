package org.icd4.commerce.adapter.elasticsearch;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import lombok.RequiredArgsConstructor;
import org.icd4.commerce.application.required.ProductCustomRepository;
import org.icd4.commerce.domain.product.Product;
import org.springframework.stereotype.Repository;

import java.io.IOException;

@RequiredArgsConstructor
@Repository
public class ProductCustomRepositoryImpl implements ProductCustomRepository {
    private final ElasticsearchClient esClient;

    @Override
    public String registerProduct(Product product) {
        try {
            IndexRequest<Product> request = IndexRequest.of(i -> i
                    .index("product_index")
                    .id(product.getProductId())
                    .document(product));
            IndexResponse response = esClient.index(request);
            return response.id();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
