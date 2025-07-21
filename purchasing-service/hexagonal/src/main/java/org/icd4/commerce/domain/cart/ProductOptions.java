package org.icd4.commerce.domain.cart;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 상품의 추가 옵션 정보를 나타내는 값 객체입니다.
 * 
 * <p>이 클래스는 장바구니 항목에 포함된 상품의 선택 옵션들을 관리하는 불변 값 객체로,
 * 도메인 주도 설계(DDD)의 값 객체 패턴을 구현합니다.</p>
 * 
 * <p>주요 특징:
 * <ul>
 *   <li>불변성(Immutability): 한 번 생성되면 옵션을 변경할 수 없습니다.</li>
 *   <li>Map 기반 구조: 키-값 쌍으로 다양한 옵션을 표현합니다.</li>
 *   <li>방어적 복사: 외부에서 전달된 Map을 복사하여 불변성을 보장합니다.</li>
 *   <li>읽기 전용 접근: getOptions()는 수정 불가능한 Map을 반환합니다.</li>
 * </ul>
 * </p>
 * 
 * <p>사용 예시:
 * <ul>
 *   <li>색상, 사이즈 등의 상품 옵션을 저장</li>
 *   <li>키: 옵션 타입(예: "color", "size"), 값: 옵션 ID</li>
 * </ul>
 * </p>
 * 
 * @since 1.
 */
public final class ProductOptions {
    
    private final Map<String, String> options;
    
    public ProductOptions(Map<String, String> options) {
        this.options = Collections.unmodifiableMap(new HashMap<>(options != null ? options : new HashMap<>()));
    }
    
    /**
     * 주어진 Map으로부터 ProductOptions를 생성합니다.
     * 
     * @param options 상품 옵션 Map (null인 경우 빈 Map으로 처리됨)
     * @return 생성된 ProductOptions 인스턴스
     */
    public static ProductOptions of(Map<String, String> options) {
        return new ProductOptions(options);
    }
    
    /**
     * 빈 ProductOptions를 생성합니다.
     * 
     * @return 옵션이 없는 ProductOptions 인스턴스
     */
    public static ProductOptions empty() {
        return new ProductOptions(new HashMap<>());
    }
    
    /**
     * 모든 옵션을 수정 불가능한 Map으로 반환합니다.
     * 
     * @return 읽기 전용 옵션 Map
     */
    public Map<String, String> getOptions() {
        return options;
    }
    
    /**
     * 특정 키의 옵션이 존재하는지 확인합니다.
     * 
     * @param key 확인할 옵션 키
     * @return 옵션이 존재하면 true, 그렇지 않으면 false
     */
    public boolean hasOption(String key) {
        return options.containsKey(key);
    }
    
    /**
     * 특정 키의 옵션 값을 반환합니다.
     * 
     * @param key 조회할 옵션 키
     * @return 옵션 값, 키가 존재하지 않으면 null
     */
    public String getOption(String key) {
        return options.get(key);
    }
    
    /**
     * 옵션이 비어있는지 확인합니다.
     * 
     * @return 옵션이 없으면 true, 하나라도 있으면 false
     */
    public boolean isEmpty() {
        return options.isEmpty();
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductOptions that = (ProductOptions) o;
        return Objects.equals(options, that.options);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(options);
    }
    
    @Override
    public String toString() {
        return "ProductOptions{" +
               "options=" + options +
               '}';
    }
}