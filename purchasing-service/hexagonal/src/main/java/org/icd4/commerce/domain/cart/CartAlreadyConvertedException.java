package org.icd4.commerce.domain.cart;

/**
 * 이미 주문으로 전환된 장바구니를 수정하려고 할 때 발생하는 예외.
 * 
 * <p>장바구니가 주문으로 전환되면 불변 상태가 되어 더 이상 수정할 수 없습니다.
 * 이 상태에서 아래와 같은 작업을 시도하면 이 예외가 발생합니다:</p>
 * <ul>
 *   <li>상품 추가 ({@link Cart#addItem})</li>
 *   <li>상품 제거 ({@link Cart#removeItem})</li>
 *   <li>수량 변경 ({@link Cart#updateQuantity})</li>
 *   <li>장바구니 비우기 ({@link Cart#clear})</li>
 *   <li>다른 장바구니 병합 ({@link Cart#merge})</li>
 *   <li>재전환 시도 ({@link Cart#convertToOrder})</li>
 * </ul>
 * 
 * <p>에러 코드: {@link CartErrorCode#CART_ALREADY_CONVERTED} (CART_001)</p>
 * 
 * @see Cart#convertToOrder()
 * @see Cart#isConverted()
 * @author Jooeun
 * @since 1.0
 */
public class CartAlreadyConvertedException extends CartException {
    public CartAlreadyConvertedException(String message) {
        super(message, CartErrorCode.CART_ALREADY_CONVERTED);
    }
}