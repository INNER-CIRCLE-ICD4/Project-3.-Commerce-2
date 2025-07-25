package org.icd4.commerce.application.provided;

import org.icd4.commerce.domain.product.model.Product;
import org.icd4.commerce.domain.product.model.ProductVariant;

public interface ProductFinder {
    Product findById(String productId);

    ProductVariant findVariantByProductIdAndSku(String productId, String skuId);
    ProductVariant findVariantBySku(String skuId);
}

