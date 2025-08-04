package org.icd4.commerce.application.provided;

import org.icd4.commerce.domain.product.model.Product;
import org.icd4.commerce.domain.product.model.VariantStatus;
import org.icd4.commerce.domain.product.request.ProductCreateRequest;
import org.icd4.commerce.domain.product.request.ProductInfoUpdateRequest;
import org.icd4.commerce.domain.product.request.ProductVariantUpdateRequest;

public interface ProductRegister {

    Product create(ProductCreateRequest request);

    Product updateInfo(String productId, String sellerId, ProductInfoUpdateRequest request);

    Product updateVariant(String productId, String sellerId, String sku, ProductVariantUpdateRequest request);

    Product updateVariantStatus(String productId, String sellerId, String sku, VariantStatus status);
}
