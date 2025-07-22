package org.icd4.commerce.application.provided;

import org.icd4.commerce.domain.product.Product;

public interface ProductModifier {
    Product changeCategory(String productId, String categoryId, String sellerId);

    Product changeProductStopped(String productId, String sellerId);

}
