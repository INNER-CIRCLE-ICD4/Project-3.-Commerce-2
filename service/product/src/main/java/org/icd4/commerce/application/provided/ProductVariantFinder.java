package org.icd4.commerce.application.provided;

import org.icd4.commerce.domain.product.model.ProductVariant;

public interface ProductVariantFinder {
    ProductVariant findProductVariantByIdAndSku(String productId, String sku);
}
