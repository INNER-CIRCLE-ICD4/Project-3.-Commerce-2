package org.icd4.commerce.domain.order;

import java.util.Objects;

/**
 * 고객의 고유 식별자를 나타내는 값 객체입니다.
 * 
 * <p>이 클래스는 장바구니 시스템에서 고객을 유일하게 식별하기 위한 불변 값 객체로,
 * 도메인 주도 설계(DDD)의 값 객체 패턴을 구현합니다.</p>
 * 
 * <p>주요 특징:
 * <ul>
 *   <li>불변성(Immutability): 한 번 생성되면 값을 변경할 수 없습니다.</li>
 *   <li>유효성 검증: null이거나 빈 값을 허용하지 않습니다.</li>
 *   <li>장바구니의 소유자를 식별하는 데 사용됩니다.</li>
 *   <li>외부 시스템의 사용자 ID와 매핑될 수 있습니다.</li>
 * </ul>
 * </p>
 * 
 * @since 1.0
 */
public final class CustomerId {
    
    private final String value;
    
    /**
     * CustomerId 생성자.
     * 
     * @param value 고객 ID 값
     * @throws NullPointerException value가 null인 경우
     * @throws IllegalArgumentException value가 빈 문자열인 경우
     */
    public CustomerId(String value) {
        Objects.requireNonNull(value, "CustomerId value cannot be null");
        if (value.trim().isEmpty()) {
            throw new IllegalArgumentException("CustomerId value cannot be empty");
        }
        this.value = value;
    }
    
    /**
     * 주어진 문자열 값으로 CustomerId를 생성합니다.
     * 
     * @param value 고객 ID 값
     * @return 생성된 CustomerId 인스턴스
     * @throws NullPointerException value가 null인 경우
     * @throws IllegalArgumentException value가 빈 문자열인 경우
     */
    public static CustomerId of(String value) {
        return new CustomerId(value);
    }
    
    public String value() {
        return value;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CustomerId that = (CustomerId) o;
        return Objects.equals(value, that.value);
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