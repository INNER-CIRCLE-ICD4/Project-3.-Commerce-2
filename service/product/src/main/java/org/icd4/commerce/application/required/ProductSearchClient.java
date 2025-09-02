package org.icd4.commerce.application.required;

import org.icd4.commerce.domain.product.model.Product;

public interface ProductSearchClient {
    String registerProduct(Product product);

    String deleteProduct(String productId);

    String updateStatus(String productId, String status);
}
