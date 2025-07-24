package org.icd4.commerce.application.provided;

import org.icd4.commerce.domain.product.model.Product;
import org.icd4.commerce.domain.product.model.ProductMoney;

public interface ProductModifier {
    Product changeCategory(String productId, String categoryId, String sellerId);

    void activate();

    void inactivate();

    void changeProductPrice(String productId, ProductMoney newPrice);
}
