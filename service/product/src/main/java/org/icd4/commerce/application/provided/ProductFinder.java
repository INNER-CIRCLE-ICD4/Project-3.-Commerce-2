package org.icd4.commerce.application.provided;

import org.icd4.commerce.domain.product.Product;

public interface ProductFinder {
    Product findById(String productId);
}
