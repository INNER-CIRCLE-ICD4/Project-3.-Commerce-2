package org.icd4.commerce.command.application.required;

import org.icd4.commerce.shared.domain.ProductCreateRequest;

import java.io.IOException;

public interface ProductDocumentIndexer {
    String indexProduct(ProductCreateRequest product) throws IOException;

    void deleteProduct(String productId);
}
