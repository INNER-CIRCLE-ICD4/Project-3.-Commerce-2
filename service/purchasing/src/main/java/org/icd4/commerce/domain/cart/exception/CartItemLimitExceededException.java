package org.icd4.commerce.domain.cart.exception;

import org.icd4.commerce.domain.cart.Cart;
import org.icd4.commerce.domain.cart.ProductId;
import org.icd4.commerce.domain.cart.ProductOptions;

/**
 * 장바구니의 상품 종류 한도를 초과했을 때 발생하는 예외.
 * 
 * <p>장바구니는 시스템 성능과 사용자 경험을 위해 최대 50개의 서로 다른 상품 타입만
 * 담을 수 있도록 제한됩니다. 이는 상품의 총 수량이 아닌 상품 종류의 개수 제한입니다.</p>
 * 
 * <p>예시:</p>
 * <ul>
 *   <li>상품 A 10개 + 상품 B 20개 = 2종류 (허용)</li>
 *   <li>49종류의 상품이 담긴 상태에서 새로운 2종류 추가 시도 = 51종류 (예외 발생)</li>
 * </ul>
 * 
 * <p>이 예외는 주로 {@link Cart#addItem} 메서드에서 발생하며,
 * {@link Cart#merge} 시에도 발생할 수 있습니다.</p>
 * 
 * <p>에러 코드: {@link CartErrorCode#CART_ITEM_LIMIT_EXCEEDED} (CART_002)</p>
 * 
 * @see Cart#addItem(ProductId, int, ProductOptions)
 * @see Cart#merge(Cart)
 * @author Jooeun
 * @since 1.0
 */
public class CartItemLimitExceededException extends CartException {
    public CartItemLimitExceededException(String message) {
        super(message, CartErrorCode.CART_ITEM_LIMIT_EXCEEDED);
    }
}