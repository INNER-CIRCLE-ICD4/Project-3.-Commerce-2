package org.icd4.commerce.domain.order;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/**
 * 주문 ID를 나타내는 Value Object.
 * 내부적으로 UUID를 감싸고 있으며,
 * 도메인 간 식별자의 타입 안정성과 의미 구분을 위해 사용됩니다.
 * 장점:
 * - 타입 안정성: 다른 ID와 혼동 방지 (예: CartId 등)
 * - 불변성 보장
 * - equals/hashCode 자동 생성 (record 문법 사용)
 */
public record OrderId(UUID value) {
    /**
     * 새 주문 ID를 생성합니다.
     * @return 새로운 OrderId (UUID 기반)
     */
    public static OrderId generate() {
        return new OrderId(UUID.randomUUID());
    }

    /**
     * OrderId를 생성
     * (예: 외부 입력값이나 DB에서 조회한 문자열을 변환할 때)
     * @param id 주문 ID 값
     * @return 생성된 orderId 인스턴스
     */
    public static OrderId from(String id) {
        return new OrderId(UUID.fromString(id));
    }

    /**
     * toString 사용 시, UUID 문자열만 나오도록 커스텀
     * @return UUID 문자열
     */
    @Override
    public String toString() {
        return value.toString();
    }
}
