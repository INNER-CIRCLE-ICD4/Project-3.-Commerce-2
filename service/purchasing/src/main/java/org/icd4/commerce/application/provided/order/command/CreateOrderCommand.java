package org.icd4.commerce.application.provided.order.command;

import java.util.List;

public record CreateOrderCommand(
        String customerId,
        List<OrderItemCommand> items,
        String orderMessage,
        String orderChannel
) {
    public CreateOrderCommand {
        if (customerId == null) {
            throw new IllegalArgumentException("customerId는 필수입니다.");
        }
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("최소 1개의 주문 항목이 필요합니다.");
        }
    }

    public record OrderItemCommand(
            String productId,
            String sku,
            Long unitPrice,
            int quantity
    ) {
        public OrderItemCommand {
            if (productId == null) {
                throw new IllegalArgumentException("productId는 필수입니다.");
            }
            if (sku == null) {
                throw new IllegalArgumentException("sku는 필수입니다.");
            }
            if (quantity < 1) {
                throw new IllegalArgumentException("quantity는 1 이상이어야 합니다.");
            }
            if (unitPrice == null || unitPrice < 0) {
                throw new IllegalArgumentException("unitPrice는 0 이상이어야 합니다.");
            }
        }
    }
}