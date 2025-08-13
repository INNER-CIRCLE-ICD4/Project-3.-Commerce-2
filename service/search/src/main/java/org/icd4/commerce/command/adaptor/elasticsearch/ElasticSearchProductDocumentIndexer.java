package org.icd4.commerce.command.adaptor.elasticsearch;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import lombok.RequiredArgsConstructor;
import org.icd4.commerce.command.application.required.ProductDocumentIndexer;
import org.icd4.commerce.command.application.required.ProductRepository;
import org.icd4.commerce.shared.domain.Product;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class ElasticSearchProductDocumentIndexer implements ProductDocumentIndexer {

    private final ElasticsearchClient esClient;
    private final ProductRepository productRepository;

    @Override
    public String indexProduct(Product product) throws IOException {
        // 실제 Elasticsearch 클라이언트를 사용하여 상품 문서를 생성 또는 업데이트하는 로직 구현
        IndexRequest<Product> indexRequest = IndexRequest.of(i -> i
            .index("product_index")
            .id(product.getId())
            .document(product)
        );
        IndexResponse index = esClient.index(indexRequest);
        return index.id();
    }

    @Override
    public int deleteProduct(String productId) {
        return productRepository.deleteById(productId);
    }
}
