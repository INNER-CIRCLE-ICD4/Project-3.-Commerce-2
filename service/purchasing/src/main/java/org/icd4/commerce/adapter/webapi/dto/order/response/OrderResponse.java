package org.icd4.commerce.adapter.webapi.dto.order.response;

import org.icd4.commerce.domain.order.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public record OrderResponse(
        String orderId,
        String customerId,
        List<OrderItemResponse> orderItems,
        String orderStatus,
        BigDecimal totalAmount,
        String orderMessage,
        LocalDateTime createdAt,
        LocalDateTime lastModifiedAt,
        String paymentId,
        String orderChannel,
        LocalDateTime completedAt
) {
    public static OrderResponse from(Order order) {
        return new OrderResponse(
                order.getOrderId().toString(),
                order.getCustomerId().toString(),
                order.getOrderItems().stream()
                        .map(OrderItemResponse::from)
                        .collect(Collectors.toList()),
                order.getOrderStatus().name(),
                order.getTotalAmount().getAmount(),
                order.getOrderMessage(),
                order.getCreatedAt(),
                order.getLastModifiedAt(),
                order.getPaymentId() != null ? order.getPaymentId().toString() : null,
                order.getOrderChannel(),
                order.getCompletedAt()
        );
    }
}