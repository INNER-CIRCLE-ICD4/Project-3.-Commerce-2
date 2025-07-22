package org.icd4.commerce.domain.cart;

/**
 * 상품의 필수 옵션이 누락되었을 때 발생하는 예외.
 * 
 * <p>일부 상품은 장바구니에 담기 위해 반드시 선택해야 하는 필수 옵션이 있습니다.
 * 이러한 필수 옵션이 선택되지 않은 상태로 장바구니에 담으려고 하면 이 예외가 발생합니다.</p>
 * 
 * <p>필수 옵션의 예시:</p>
 * <ul>
 *   <li>의류: 사이즈(S, M, L, XL), 색상</li>
 *   <li>신발: 사이즈(250, 260, 270 등)</li>
 *   <li>전자제품: 모델, 용량, 색상</li>
 *   <li>맞춤 제작 상품: 각인 문구, 디자인 옵션</li>
 * </ul>
 * 
 * <p>이 예외는 {@link ProductOptions}에 필수 옵션이 포함되지 않았을 때 발생하며,
 * 상품 서비스와의 연동을 통해 각 상품의 필수 옵션 여부를 검증합니다.</p>
 * 
 * <p>에러 코드: {@link CartErrorCode#REQUIRED_OPTION_MISSING} (CART_005)</p>
 * 
 * @see Cart#addItem(ProductId, int, ProductOptions)
 * @see ProductOptions
 * @author Jooeun
 * @since 1.0
 */
public class RequiredOptionMissingException extends CartException {
    public RequiredOptionMissingException(String message) {
        super(message, CartErrorCode.REQUIRED_OPTION_MISSING);
    }
}