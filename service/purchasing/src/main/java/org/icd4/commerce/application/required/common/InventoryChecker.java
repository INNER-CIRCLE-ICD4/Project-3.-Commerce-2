package org.icd4.commerce.application.required.common;

import org.icd4.commerce.domain.common.ProductId;

/**
 * 재고 확인 인터페이스.
 * 
 * <p>장바구니에 상품을 추가하거나 수량을 변경할 때
 * 재고를 확인하기 위한 포트입니다.</p>
 */
public interface InventoryChecker {
    
    /**
     * 상품의 구매 가능한 재고를 확인합니다.
     * 
     * @param productId 상품 ID
     * @return 구매 가능한 재고 수량
     * @throws IllegalArgumentException 상품을 찾을 수 없는 경우
     */
    int getAvailableStock(ProductId productId);
    
    /**
     * 요청한 수량만큼 재고가 있는지 확인합니다.
     * 
     * @param productId 상품 ID
     * @param requestedQuantity 요청 수량
     * @return 재고가 충분하면 true
     */
    default boolean hasStock(ProductId productId, int requestedQuantity) {
        return getAvailableStock(productId) >= requestedQuantity;
    }
}