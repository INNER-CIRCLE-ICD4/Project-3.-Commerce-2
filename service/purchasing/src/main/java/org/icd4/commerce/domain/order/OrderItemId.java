package org.icd4.commerce.domain.order;

import org.icd4.commerce.common.idgenerator.ULIDUtils;

/**
 * 주문 상품의 고유 식별자를 나타내는 값 객체입니다.
 * <p>주문 시스템 내에서 개별 주문 항목을 구분하기 위해 사용됩니다.</p>
 */
public record OrderItemId(String value) {

    public OrderItemId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("OrderItemId는 null일 수 없습니다.");
        }
    }

    public static OrderItemId generate() {
        return new OrderItemId(ULIDUtils.generate());
    }

    public static OrderItemId of(String value) {
        return new OrderItemId(value);
    }

    @Override
    public String toString() {
        return value;
    }

}
