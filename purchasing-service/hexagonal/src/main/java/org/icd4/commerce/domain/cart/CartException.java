package org.icd4.commerce.domain.cart;

/**
 * Cart 도메인 예외의 기본 클래스.
 * 
 * <p>장바구니 도메인에서 발생하는 모든 예외의 부모 클래스입니다.
 * 각 예외는 고유한 {@link CartErrorCode}를 포함하여 에러 유형을 식별할 수 있습니다.</p>
 * 
 * <p>이 예외 클래스를 상속하는 구체적인 예외들:</p>
 * <ul>
 *   <li>{@link CartAlreadyConvertedException} - 이미 주문으로 전환된 장바구니 수정 시도</li>
 *   <li>{@link CartItemLimitExceededException} - 장바구니 상품 종류 한도 초과</li>
 *   <li>{@link InvalidCartStateException} - 잘못된 장바구니 상태</li>
 *   <li>{@link InvalidQuantityException} - 잘못된 수량 입력</li>
 *   <li>{@link RequiredOptionMissingException} - 필수 옵션 누락</li>
 * </ul>
 * 
 * @see CartErrorCode
 * @author Jooeun
 * @since 1.0
 */
public abstract class CartException extends RuntimeException {
    private final CartErrorCode errorCode;
    
    protected CartException(String message, CartErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
    
    protected CartException(String message, CartErrorCode errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
    
    public CartErrorCode getErrorCode() {
        return errorCode;
    }
}