package org.icd4.commerce.application.provided;

import org.icd4.commerce.domain.product.model.Product;
import org.icd4.commerce.domain.product.request.ProductCreateRequest;
import org.icd4.commerce.domain.product.request.ProductInfoUpdateRequest;
import org.icd4.commerce.domain.product.request.ProductVariantUpdateRequest;

public interface ProductRegister {

    Product create(ProductCreateRequest request);

    Product updateInfo(String productId, ProductInfoUpdateRequest request);

    Product updateVariant(String productId, String sku, ProductVariantUpdateRequest request);
}
