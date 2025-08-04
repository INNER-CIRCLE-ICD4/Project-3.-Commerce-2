package org.icd4.commerce.application.provided;

import org.icd4.commerce.domain.product.model.Product;

public interface ProductFinder {
    Product findById(String productId);

    Product findByIdAndSellerId(String productId, String sellerId);

    Product findProductWithVariantsByIdAndSellerId(String productId, String sellerId);
}

