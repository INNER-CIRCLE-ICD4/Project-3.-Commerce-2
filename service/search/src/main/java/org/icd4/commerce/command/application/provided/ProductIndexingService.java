package org.icd4.commerce.command.application.provided;

import lombok.RequiredArgsConstructor;
import org.icd4.commerce.command.adaptor.elasticsearch.ElasticSearchProductDocumentIndexer;
import org.icd4.commerce.shared.domain.ProductCreateRequest;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class ProductIndexingService {
    private final ElasticSearchProductDocumentIndexer elasticSearchProductDocumentIndexer;

    public String indexing(ProductCreateRequest request) throws IOException {
        return elasticSearchProductDocumentIndexer.indexProduct(request);
    }

    public void delete(String productId) {
        elasticSearchProductDocumentIndexer.deleteProduct(productId);
    }
}
