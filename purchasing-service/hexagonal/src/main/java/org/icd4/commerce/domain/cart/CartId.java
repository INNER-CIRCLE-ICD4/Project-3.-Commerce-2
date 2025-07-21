package org.icd4.commerce.domain.cart;

import java.util.Objects;
import java.util.UUID;

/**
 * 장바구니의 고유 식별자를 나타내는 값 객체입니다.
 * 
 * <p>이 클래스는 장바구니를 유일하게 식별하기 위한 불변 값 객체로,
 * 도메인 주도 설계(DDD)의 값 객체 패턴을 구현합니다.</p>
 * 
 * <p>주요 특징:
 * <ul>
 *   <li>불변성(Immutability): 한 번 생성되면 값을 변경할 수 없습니다.</li>
 *   <li>유효성 검증: null이거나 빈 값을 허용하지 않습니다.</li>
 *   <li>UUID 기반 생성: 자동 생성 시 UUID를 사용하여 고유성을 보장합니다.</li>
 * </ul>
 * </p>
 * 
 * @since 1.0
 */
public final class CartId {
    
    private final String value;
    
    /**
     * CartId 생성자.
     * 
     * @param value 장바구니 ID 값
     * @throws NullPointerException value가 null인 경우
     * @throws IllegalArgumentException value가 빈 문자열인 경우
     */
    public CartId(String value) {
        Objects.requireNonNull(value, "CartId value cannot be null");
        if (value.trim().isEmpty()) {
            throw new IllegalArgumentException("CartId value cannot be empty");
        }
        this.value = value;
    }
    
    /**
     * 주어진 문자열 값으로 CartId를 생성합니다.
     * 
     * @param value 장바구니 ID 값
     * @return 생성된 CartId 인스턴스
     * @throws NullPointerException value가 null인 경우
     * @throws IllegalArgumentException value가 빈 문자열인 경우
     */
    public static CartId of(String value) {
        return new CartId(value);
    }
    
    /**
     * UUID를 사용하여 새로운 CartId를 자동 생성합니다.
     * 
     * @return UUID 기반의 새로운 CartId 인스턴스
     */
    public static CartId generate() {
        return new CartId(UUID.randomUUID().toString());
    }
    
    public String value() {
        return value;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CartId cartId = (CartId) o;
        return Objects.equals(value, cartId.value);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
    
    @Override
    public String toString() {
        return value;
    }
}