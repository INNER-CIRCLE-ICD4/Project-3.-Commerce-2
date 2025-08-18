package org.icd4.commerce.domain.order;

/**
 * 주문 상품의 고유 식별자를 나타내는 값 객체입니다.
 * <p>주문 시스템 내에서 개별 주문 항목을 구분하기 위해 사용됩니다.</p>
 */
public record OrderItemId(int value) {

    public OrderItemId {
        if (value <= 0) {
            throw new IllegalArgumentException("itemId는 1 이상이어야 합니다. itemId=" + value);
        }
    }

    /**
     * 새 OrderItemId 생성
     */
    public static OrderItemId of(int itemNo) {
        return new OrderItemId(itemNo);
    }

}
