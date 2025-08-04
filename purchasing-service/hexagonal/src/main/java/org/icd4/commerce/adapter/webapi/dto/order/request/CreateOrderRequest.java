package org.icd4.commerce.adapter.webapi.dto.order.request;

import java.util.List;
import org.icd4.commerce.application.provided.order.command.CreateOrderCommand;

public record CreateOrderRequest(
        String customerId,
        List<OrderItemRequest> orderItems,
        String orderMessage,
        String orderChannel
) {
    public CreateOrderCommand toCommand() {
        return new CreateOrderCommand(
                customerId,
                orderItems.stream()
                        .map(OrderItemRequest::toCommand)
                        .toList(),
                orderMessage,
                orderChannel
        );
    }

    public record OrderItemRequest(
            Long productId,
            long unitPrice,
            Long quantity
    ) {
        public CreateOrderCommand.OrderItemCommand toCommand() {
            return new CreateOrderCommand.OrderItemCommand(
                    productId,
                    unitPrice,
                    quantity
            );
        }
    }
}
