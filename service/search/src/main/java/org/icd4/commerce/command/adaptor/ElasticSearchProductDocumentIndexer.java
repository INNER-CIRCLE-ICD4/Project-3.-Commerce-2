package org.icd4.commerce.command.adaptor;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import lombok.RequiredArgsConstructor;
import org.icd4.commerce.command.application.ProductDocumentIndexer;
import org.icd4.commerce.shared.domain.Product;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class ElasticSearchProductDocumentIndexer implements ProductDocumentIndexer {

    private final ElasticsearchClient esClient;

    @Override
    public void indexProduct(Product product) throws IOException {
        // 실제 Elasticsearch 클라이언트를 사용하여 상품 문서를 생성 또는 업데이트하는 로직 구현
        IndexRequest<Product> indexRequest = IndexRequest.of(i -> i
            .index("product-index")
            .id(product.getId())
            .document(product)
        );
        esClient.index(indexRequest);
    }

    @Override
    public void deleteProduct(String productId) throws IOException {
        // 실제 엘라스틱서치에 도큐먼트 삭제 로직
    }
}
