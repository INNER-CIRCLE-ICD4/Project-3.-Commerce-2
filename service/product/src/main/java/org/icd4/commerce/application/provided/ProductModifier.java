package org.icd4.commerce.application.provided;

import org.icd4.commerce.domain.product.model.Product;
import org.icd4.commerce.domain.product.model.ProductMoney;

public interface ProductModifier {
    Product changeCategory(String productId, String sellerId, String categoryId);

    Product activate(String productId, String sellerId);

    Product inactivate(String productId, String sellerId);

    Product deleteProduct(String productId, String sellerId);

    Product changeProductPrice(String productId, String sellerId, ProductMoney newPrice);
}
