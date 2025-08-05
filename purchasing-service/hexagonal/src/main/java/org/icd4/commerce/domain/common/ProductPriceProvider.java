package org.icd4.commerce.domain.common;

import org.icd4.commerce.domain.cart.Cart;

import java.math.BigDecimal;

/**
 * 상품 가격 조회 인터페이스.
 * 
 * <p>장바구니 도메인이 상품의 가격 정보를 조회하기 위한 포트입니다.
 * 헥사고날 아키텍처에서 도메인 레이어가 외부 시스템(상품 서비스)에
 * 의존하지 않도록 추상화된 인터페이스를 제공합니다.</p>
 * 
 * <p>구현체는 다음과 같은 방식으로 제공될 수 있습니다:</p>
 * <ul>
 *   <li>상품 서비스 API 호출</li>
 *   <li>로컬 캐시 조회</li>
 *   <li>데이터베이스 직접 조회</li>
 * </ul>
 * 
 * @author Jooeun
 * @since 1.0
 * @see Cart#calculateTotal(ProductPriceProvider)
 */
public interface ProductPriceProvider {
    /**
     * 특정 상품의 현재 가격을 조회합니다.
     * 
     * @param productId 가격을 조회할 상품의 식별자
     * @return 상품의 현재 가격
     * @throws NullPointerException productId가 null인 경우
     * @throws IllegalArgumentException 존재하지 않는 상품인 경우
     */
    BigDecimal getPrice(ProductId productId);
}