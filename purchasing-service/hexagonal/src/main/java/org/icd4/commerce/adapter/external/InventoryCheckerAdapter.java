package org.icd4.commerce.adapter.external;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.icd4.commerce.domain.cart.InventoryChecker;
import org.icd4.commerce.domain.cart.ProductId;
import org.springframework.stereotype.Component;

/**
 * InventoryChecker의 어댑터 구현체.
 * 
 * <p>외부 상품 서비스를 통해 재고를 확인합니다.</p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class InventoryCheckerAdapter implements InventoryChecker {
    
    private final ProductServiceClient productServiceClient;
    
    @Override
    public int getAvailableStock(ProductId productId) {
        if (productId == null) {
            throw new NullPointerException("ProductId cannot be null");
        }
        
        try {
            log.debug("Checking stock for product: {}", productId);
            
            int stock = productServiceClient.getAvailableStock(productId);
            
            log.debug("Available stock for product {}: {}", productId, stock);
            
            return stock;
            
        } catch (ProductNotFoundException e) {
            throw new IllegalArgumentException(
                "Product not found: " + productId.value(), e
            );
        }
    }
}