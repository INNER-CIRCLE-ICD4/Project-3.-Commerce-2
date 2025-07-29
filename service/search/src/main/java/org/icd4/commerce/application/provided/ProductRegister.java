package org.icd4.commerce.application.provided;

import org.icd4.commerce.adapter.webapi.dto.ProductCreateRequest;
import org.icd4.commerce.domain.product.Product;

public interface ProductRegister {

    Product createProduct(ProductCreateRequest request);
}
