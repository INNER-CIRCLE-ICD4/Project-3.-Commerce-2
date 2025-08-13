package org.icd4.commerce.command.application.required;

import org.icd4.commerce.shared.domain.Product;

import java.io.IOException;

public interface ProductDocumentIndexer {
    String indexProduct(Product product) throws IOException;
    int deleteProduct(String productId);
}
