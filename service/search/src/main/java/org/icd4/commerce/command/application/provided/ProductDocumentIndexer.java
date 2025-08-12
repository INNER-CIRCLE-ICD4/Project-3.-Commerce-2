package org.icd4.commerce.command.application.provided;

import org.icd4.commerce.shared.domain.Product;

import java.io.IOException;

public interface ProductDocumentIndexer {
    void indexProduct(Product product) throws IOException;
    void deleteProduct(String productId) throws IOException;
}
