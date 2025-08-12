package org.icd4.commerce.domain.order;

import lombok.Getter;
import org.icd4.commerce.domain.common.ProductId;

import java.util.Map;
import java.util.Objects;

/**
 * 주문에 포함된 개별 상품 항목을 나타내는 엔티티입니다.
 * <p>주문 시점의 상품명, 가격, 수량, 옵션 정보를 스냅샷 형태로 보존합니다.</p>
 */
@Getter
public class OrderItem {
    private final OrderItemId orderItemId;
    private final OrderId orderId;
    private final ProductId productId;
    private final String productName;
    private final long unitPrice;
    private final long quantity;
    private final long itemAmount; // = unitPrice * quantity
    private final Map<String, String> productOptions; // 옵션 타입 ID → 옵션 값 ID

    /**
     * 생성자 - 검증 포함
     */
    public OrderItem(
            OrderItemId orderItemId,
            OrderId orderId,
            ProductId productId,
            String productName,
            long unitPrice,
            long quantity,
            Map<String, String> productOptions
    ) {
        this.orderItemId = Objects.requireNonNull(orderItemId, "orderItemId is required");
        this.orderId = Objects.requireNonNull(orderId, "orderId is required");
        this.productId = Objects.requireNonNull(productId, "productId is required");
        this.productOptions = Objects.requireNonNull(productOptions, "productOptions is required");

        if (productName == null || productName.isBlank()) {
            throw new IllegalArgumentException("상품명은 비어 있을 수 없습니다.");
        }
        if (unitPrice < 0) {
            throw new IllegalArgumentException("상품 가격은 0 이상이어야 합니다.");
        }
        if (quantity < 1) {
            throw new IllegalArgumentException("상품 수량은 1개 이상이어야 합니다.");
        }

        this.productName = productName;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
        this.itemAmount = calculateItemAmount();
    }

    /**
     * 상품별 총 금액 계산
     */
    public long calculateItemAmount() {
        return unitPrice * quantity;
    }

}
