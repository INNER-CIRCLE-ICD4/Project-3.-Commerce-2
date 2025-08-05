package org.icd4.commerce.command.application.provide;

import org.icd4.commerce.shared.domain.Product;
import org.icd4.commerce.shared.domain.ProductUpdateRequest;

import java.math.BigDecimal;

public interface ProductModifier {

    void modifyProductInfo(Product product);

    void modifyProductPrice(String productId, BigDecimal newPrice);

    void modifyProductStatus(String productId, String status);

    void modifyProductVariantPrice(String productId, String sku, BigDecimal newPrice);

    void modifyProductVariantStock(String productId, String sku, Integer newStock);

    void modifyProductVariantStatus(String productId, ProductUpdateRequest.ProductVariantDto variants);

}
