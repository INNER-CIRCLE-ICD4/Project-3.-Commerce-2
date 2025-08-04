package org.icd4.commerce.domain.order;

import java.util.Objects;

/**
 * 상품의 고유 식별자를 나타내는 값 객체입니다.
 * 
 * <p>이 클래스는 장바구니 시스템에서 상품을 유일하게 식별하기 위한 불변 값 객체로,
 * 도메인 주도 설계(DDD)의 값 객체 패턴을 구현합니다.</p>
 * 
 * <p>주요 특징:
 * <ul>
 *   <li>불변성(Immutability): 한 번 생성되면 값을 변경할 수 없습니다.</li>
 *   <li>유효성 검증: null이거나 0 이하의 값을 허용하지 않습니다.</li>
 *   <li>Long 타입 사용: 데이터베이스의 자동 증가 ID와 호환됩니다.</li>
 *   <li>장바구니 항목이 참조하는 상품을 식별합니다.</li>
 * </ul>
 * </p>
 * 
 * @since 1.0
 */
public final class ProductId {
    
    private final Long value;
    
    /**
     * ProductId 생성자.
     * 
     * @param value 상품 ID 값
     * @throws NullPointerException value가 null인 경우
     * @throws IllegalArgumentException value가 0 이하인 경우
     */
    public ProductId(Long value) {
        Objects.requireNonNull(value, "ProductId value cannot be null");
        if (value <= 0) {
            throw new IllegalArgumentException("ProductId value must be positive");
        }
        this.value = value;
    }
    
    /**
     * 주어진 Long 값으로 ProductId를 생성합니다.
     * 
     * @param value 상품 ID 값
     * @return 생성된 ProductId 인스턴스
     * @throws NullPointerException value가 null인 경우
     * @throws IllegalArgumentException value가 0 이하인 경우
     */
    public static ProductId of(Long value) {
        return new ProductId(value);
    }
    
    public Long value() {
        return value;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductId productId = (ProductId) o;
        return Objects.equals(value, productId.value);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
    
    @Override
    public String toString() {
        return value.toString();
    }
}