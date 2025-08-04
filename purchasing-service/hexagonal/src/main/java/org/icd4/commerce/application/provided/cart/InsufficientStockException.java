package org.icd4.commerce.application.provided.cart;

import org.icd4.commerce.domain.cart.ProductId;

/**
 * 재고가 부족할 때 발생하는 예외.
 */
public class InsufficientStockException extends RuntimeException {
    
    private final ProductId productId;
    private final int availableStock;
    private final int requestedQuantity;
    
    public InsufficientStockException(
            ProductId productId, 
            int availableStock, 
            int requestedQuantity) {
        super(String.format(
            "Insufficient stock for product %s. Available: %d, Requested: %d",
            productId.value(), availableStock, requestedQuantity
        ));
        this.productId = productId;
        this.availableStock = availableStock;
        this.requestedQuantity = requestedQuantity;
    }
    
    public ProductId getProductId() {
        return productId;
    }
    
    public int getAvailableStock() {
        return availableStock;
    }
    
    public int getRequestedQuantity() {
        return requestedQuantity;
    }
}