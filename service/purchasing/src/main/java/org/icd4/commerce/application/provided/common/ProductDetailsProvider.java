package org.icd4.commerce.application.provided.common;

import org.icd4.commerce.adapter.external.ProductDetailsProviderAdapter;
import org.icd4.commerce.adapter.external.ProductServiceClient;
import org.icd4.commerce.domain.common.ProductId;

import java.math.BigDecimal;

public interface ProductDetailsProvider {

    ProductDetails getProductInfo(ProductId productId);

    record ProductDetails(String name, BigDecimal price, boolean active) {}
}
