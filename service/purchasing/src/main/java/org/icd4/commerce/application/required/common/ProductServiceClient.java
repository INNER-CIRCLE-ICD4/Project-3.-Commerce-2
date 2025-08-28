package org.icd4.commerce.application.required.common;


import org.icd4.commerce.domain.common.ProductId;
import org.icd4.commerce.domain.common.StockKeepingUnit;

import java.math.BigDecimal;

public interface ProductServiceClient {

    ProductInfo getProduct(ProductId productId, StockKeepingUnit sku);

    record ProductInfo(
            String productId,
            String sku,
            String name,
            BigDecimal price,
            boolean isActive
    ) {
    }
}