package org.icd4.commerce.application.required;

import org.icd4.commerce.domain.product.Product;

public interface ProductCustomRepository {
    String registerProduct(Product product);
}
