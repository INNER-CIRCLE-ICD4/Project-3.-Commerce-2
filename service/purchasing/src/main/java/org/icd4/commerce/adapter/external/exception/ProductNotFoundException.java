package org.icd4.commerce.adapter.external.exception;

import org.icd4.commerce.domain.common.ProductId;

/**
 * 상품을 찾을 수 없을 때 발생하는 예외.
 */
public class ProductNotFoundException extends RuntimeException {
    
    private final ProductId productId;
    
    public ProductNotFoundException(ProductId productId) {
        super(String.format("Product not found: %s", productId.value()));
        this.productId = productId;
    }
    
    public ProductId getProductId() {
        return productId;
    }
}