package org.icd4.commerce.command.application.required;

import org.icd4.commerce.shared.domain.Product;

import java.math.BigDecimal;

public interface ProductCommandCustomRepository {
    String registerProduct(Product product);
    void updateProduct(Product product);
    void updateProductPrice(String productId, BigDecimal newPrice);
    void updateProductStatus(String productId, String status);
    void updateProductVariantPrice(String productId, String sku, BigDecimal newPrice);
    void updateProductVariantStock(String productId, String sku, Integer newStock);
}
