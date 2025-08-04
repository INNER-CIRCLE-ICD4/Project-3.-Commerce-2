package org.icd4.commerce.domain.cart.exception;

import org.icd4.commerce.domain.cart.Cart;
import org.icd4.commerce.domain.cart.CartItemId;

/**
 * 장바구니가 유효하지 않은 상태일 때 발생하는 예외.
 * 
 * <p>이 예외는 장바구니 작업 시 논리적으로 유효하지 않은 상태를 감지했을 때 발생합니다.
 * 다음과 같은 상황에서 발생할 수 있습니다:</p>
 * <ul>
 *   <li>빈 장바구니를 주문으로 전환하려고 시도할 때</li>
 *   <li>존재하지 않는 장바구니 아이템을 제거하려고 할 때</li>
 *   <li>존재하지 않는 장바구니 아이템의 수량을 변경하려고 할 때</li>
 *   <li>이미 전환된 장바구니를 병합하려고 할 때</li>
 * </ul>
 * 
 * <p>이 예외는 일반적으로 클라이언트의 잘못된 요청이나 동시성 문제로 인해 발생합니다.</p>
 * 
 * <p>에러 코드: {@link CartErrorCode#INVALID_CART_STATE} (CART_003)</p>
 * 
 * @see Cart#removeItem(CartItemId)
 * @see Cart#updateQuantity(CartItemId, int)
 * @see Cart#convertToOrder()
 * @see Cart#merge(Cart)
 * @author Jooeun
 * @since 1.0
 */
public class InvalidCartStateException extends CartException {
    public InvalidCartStateException(String message) {
        super(message, CartErrorCode.INVALID_CART_STATE);
    }
}