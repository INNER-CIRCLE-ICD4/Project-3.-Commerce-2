package org.icd4.commerce.domain.cart;

/**
 * Cart 도메인 에러 코드 정의.
 * 
 * <p>장바구니 도메인에서 발생하는 모든 에러를 분류하고 식별하기 위한 코드입니다.
 * 각 에러 코드는 고유한 코드값과 기본 메시지를 가지며, API 응답이나 로깅에 활용됩니다.</p>
 * 
 * <p>에러 코드 체계:</p>
 * <ul>
 *   <li>CART_001 ~ CART_099: 장바구니 관련 에러</li>
 *   <li>코드 형식: CART_XXX (XXX는 일련번호)</li>
 * </ul>
 * 
 * @author Jooeun
 * @since 1.0
 */
public enum CartErrorCode {
    /**
     * 이미 주문으로 전환된 장바구니를 수정하려고 할 때 발생.
     * 장바구니가 주문으로 전환되면 더 이상 수정할 수 없는 불변 상태가 됩니다.
     */
    CART_ALREADY_CONVERTED("CART_001", "Cart is already converted to order"),
    
    /**
     * 장바구니에 담을 수 있는 상품 종류의 한도(50개)를 초과했을 때 발생.
     * 서로 다른 상품의 개수를 제한하여 시스템 성능을 보호합니다.
     */
    CART_ITEM_LIMIT_EXCEEDED("CART_002", "Cart item limit exceeded"),
    
    /**
     * 장바구니가 유효하지 않은 상태일 때 발생.
     * 예: 빈 장바구니를 주문으로 전환, 존재하지 않는 아이템 수정 등
     */
    INVALID_CART_STATE("CART_003", "Invalid cart state"),
    
    /**
     * 잘못된 수량을 입력했을 때 발생.
     * 수량은 1 이상 99 이하의 정수여야 합니다.
     */
    INVALID_QUANTITY("CART_004", "Invalid quantity"),
    
    /**
     * 상품의 필수 옵션이 누락되었을 때 발생.
     * 일부 상품은 사이즈, 색상 등의 필수 옵션을 선택해야 합니다.
     */
    REQUIRED_OPTION_MISSING("CART_005", "Required option is missing"),
    
    /**
     * 재고가 부족할 때 발생.
     * 요청한 수량보다 재고가 적을 경우 이 에러가 발생합니다.
     */
    INSUFFICIENT_STOCK("CART_006", "Insufficient stock");
    
    private final String code;
    private final String defaultMessage;
    
    CartErrorCode(String code, String defaultMessage) {
        this.code = code;
        this.defaultMessage = defaultMessage;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getDefaultMessage() {
        return defaultMessage;
    }
}