package org.icd4.commerce.domain.cart.exception;

import org.icd4.commerce.domain.cart.*;

/**
 * 잘못된 수량을 입력했을 때 발생하는 예외.
 * 
 * <p>장바구니 아이템의 수량은 다음 규칙을 따라야 합니다:</p>
 * <ul>
 *   <li>최소값: 1개 (0개 이하는 허용되지 않음)</li>
 *   <li>최대값: 99개 (시스템 제한)</li>
 *   <li>정수값만 허용 (소수점 불가)</li>
 * </ul>
 * 
 * <p>이 예외가 발생하는 상황:</p>
 * <ul>
 *   <li>0 이하의 수량으로 상품 추가 시도</li>
 *   <li>100개 이상의 수량으로 상품 추가 시도</li>
 *   <li>기존 아이템의 수량을 잘못된 값으로 변경 시도</li>
 *   <li>수량 증가로 인해 총 수량이 99개를 초과하는 경우</li>
 * </ul>
 * 
 * <p>에러 코드: {@link CartErrorCode#INVALID_QUANTITY} (CART_004)</p>
 * 
 * @see Cart#addItem(ProductId, int, ProductOptions)
 * @see Cart#updateQuantity(CartItemId, int)
 * @see CartItem#increaseQuantity(int)
 * @see CartItem#updateQuantity(int)
 * @author Jooeun
 * @since 1.0
 */
public class InvalidQuantityException extends CartException {
    public InvalidQuantityException(String message) {
        super(message, CartErrorCode.INVALID_QUANTITY);
    }
}