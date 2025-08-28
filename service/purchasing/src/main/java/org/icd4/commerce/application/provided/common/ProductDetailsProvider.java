package org.icd4.commerce.application.provided.common;

import org.icd4.commerce.domain.common.ProductId;
import org.icd4.commerce.domain.common.StockKeepingUnit;

import java.math.BigDecimal;

public interface ProductDetailsProvider {

    ProductDetails getProductInfo(ProductId productId, StockKeepingUnit sku);

    record ProductDetails(String name, BigDecimal price, boolean active) {}
}
