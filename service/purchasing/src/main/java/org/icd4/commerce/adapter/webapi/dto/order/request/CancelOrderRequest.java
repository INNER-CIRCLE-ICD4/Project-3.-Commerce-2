package org.icd4.commerce.adapter.webapi.dto.order.request;

import org.icd4.commerce.application.provided.order.command.CancelOrderCommand;
import org.icd4.commerce.domain.order.OrderId;
import org.icd4.commerce.domain.order.OrderStatus;

import java.util.UUID;

public record CancelOrderRequest(OrderStatus status) {
    public CancelOrderCommand toCommand(String orderId) {
        return new CancelOrderCommand(OrderId.from(orderId));
    }
}
