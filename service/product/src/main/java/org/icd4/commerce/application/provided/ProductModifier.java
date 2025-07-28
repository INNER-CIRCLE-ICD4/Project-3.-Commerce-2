package org.icd4.commerce.application.provided;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.icd4.commerce.domain.product.model.Product;
import org.icd4.commerce.domain.product.model.ProductMoney;

public interface ProductModifier {
    Product changeCategory(String productId, String categoryId, String sellerId);

    void activate(String productId, String sellerId);

    void inactivate(String productId, String sellerId);

    void deleteProduct(String productId, String sellerId);

    void changeProductPrice(String productId, String sellerId, ProductMoney newPrice);
}
