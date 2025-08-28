package org.icd4.commerce.query.application.provided;

import org.icd4.commerce.shared.domain.Product;

public interface ProductDocumentFinder {
    Product findById(String productId);
}
