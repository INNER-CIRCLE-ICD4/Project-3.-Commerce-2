package org.icd4.commerce.command.application.provided;

import lombok.RequiredArgsConstructor;
import org.icd4.commerce.command.adaptor.elasticsearch.ElasticSearchProductDocumentIndexer;
import org.icd4.commerce.shared.domain.Product;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class ProductIndexingService {
    private final ElasticSearchProductDocumentIndexer elasticSearchProductDocumentIndexer;

    public String indexing(Product product) throws IOException {
        return elasticSearchProductDocumentIndexer.indexProduct(product);
    }

    public int delete(String productId) {
    private final ProductDocumentIndexer productDocumentIndexer;

    public String indexing(Product product) throws IOException {
        return productDocumentIndexer.indexProduct(product);
    }

    public int delete(String productId) {
        return productDocumentIndexer.deleteProduct(productId);
    }
}
