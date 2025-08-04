package org.icd4.commerce.adapter.webapi.dto.order.response;

import org.icd4.commerce.domain.order.OrderItem;

import java.util.Map;

public record OrderItemResponse(
        String orderItemId,
        String productId,
        String productName,
        long unitPrice,
        long quantity,
        long itemAmount,
        Map<String, String> productOptions
) {
    public static OrderItemResponse from(OrderItem item) {
        return new OrderItemResponse(
                item.getOrderItemId().toString(),
                item.getProductId().toString(),
                item.getProductName(),
                item.getUnitPrice(),
                item.getQuantity(),
                item.getItemAmount(),
                item.getProductOptions()
        );
    }
}
