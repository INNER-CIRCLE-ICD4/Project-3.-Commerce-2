package org.icd4.commerce.domain.product.request;

import org.icd4.commerce.domain.product.model.ProductMoney;

public record ProductPriceUpdateRequest(
        ProductMoney price
) {
}
