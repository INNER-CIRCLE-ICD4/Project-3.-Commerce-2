package org.icd4.commerce.domain.order;

import java.util.UUID;

/**
 * 주문 ID를 나타내는 Value Object
 */
public record OrderId(String value) {
    /**
     * 새 주문 ID를 생성합니다.
     * @return 새로운 OrderId (UUID 기반)
     */
    public static OrderId generate() {
        return new OrderId(UUID.randomUUID().toString());
    }

    /**
     * OrderId를 생성
     * (예: 외부 입력값이나 DB에서 조회한 문자열을 변환할 때)
     * @param id 주문 ID 값
     * @return 생성된 orderId 인스턴스
     */
    public static OrderId from(String id) {
        return new OrderId(id);
    }
}
