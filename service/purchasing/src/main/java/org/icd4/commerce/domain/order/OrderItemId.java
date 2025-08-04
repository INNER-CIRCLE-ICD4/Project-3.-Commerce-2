package org.icd4.commerce.domain.order;

import java.util.Objects;
import java.util.UUID;

/**
 * 주문 상품의 고유 식별자를 나타내는 값 객체입니다.
 * <p>주문 시스템 내에서 개별 주문 항목을 구분하기 위해 사용됩니다.</p>
 */
public record OrderItemId(UUID value) {

    public OrderItemId {
        Objects.requireNonNull(value,"OrderItemId는 null이거나 비어 있을 수 없습니다.");
    }

    /**
     * 새 OrderItemId 생성
     */
    public static OrderItemId generate() {
        return new OrderItemId(UUID.randomUUID());
    }

    /**
     * 문자열로부터 OrderItemId 생성
     */
    public static OrderItemId from(String id) {
        return new OrderItemId(UUID.fromString(id));
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
