package org.icd4.commerce.application.provided;

import org.icd4.commerce.domain.product.Product;
import org.icd4.commerce.domain.product.ProductCreateRequest;
import org.icd4.commerce.domain.product.ProductInfoUpdateRequest;

public interface ProductRegister {

    Product create(ProductCreateRequest request);

    Product updateInfo(String productId, ProductInfoUpdateRequest request);
}
