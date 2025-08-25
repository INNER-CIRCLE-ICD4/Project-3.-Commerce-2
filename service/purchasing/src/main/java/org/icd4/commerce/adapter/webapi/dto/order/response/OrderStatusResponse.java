package org.icd4.commerce.adapter.webapi.dto.order.response;

import io.swagger.v3.oas.annotations.media.Schema;
import org.icd4.commerce.adapter.webapi.dto.cart.response.CartItemResponse;
import org.icd4.commerce.domain.order.Order;

import java.time.LocalDateTime;

@Schema(description = "주문 상태 응답 DTO")
public record OrderStatusResponse(
        @Schema(description = "주문 ID")
        String orderId,

        @Schema(description = "현재 상태", example = "PAID")
        String status,

        @Schema(description = "마지막 변경 시각", example = "2025-08-13T15:30:00")
        LocalDateTime lastModifiedAt
) {
    public static OrderStatusResponse from(Order order) {
        return  new OrderStatusResponse(
                order.getOrderId().value(),
                order.getOrderStatus().name(),
                order.getLastModifiedAt()

        );
    }
}
