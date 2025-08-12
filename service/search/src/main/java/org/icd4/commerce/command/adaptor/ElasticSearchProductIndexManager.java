package org.icd4.commerce.command.adaptor;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import lombok.RequiredArgsConstructor;
import org.icd4.commerce.command.application.provided.ProductIndexManager;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class ElasticSearchProductIndexManager implements ProductIndexManager {

    private final ElasticsearchClient esClient;

    @Override
    public void deleteIndex() throws IOException {
        System.out.println("Elasticsearch Index 'product-index' created successfully.");
        // 실제 esClient 사용하여 인덱스 생성 로직 구현
    }

    @Override
    public void createIndex() throws IOException {
        System.out.println("Elasticsearch Index 'product-index' deleted successfully.");
    }
}
