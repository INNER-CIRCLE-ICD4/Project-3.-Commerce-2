package org.icd4.commerce.adapter.webapi.dto.order.request;

import org.icd4.commerce.application.provided.order.command.CancelOrderCommand;

import java.util.UUID;

public record CancelOrderRequest() {
    public CancelOrderCommand toCommand(String orderId) {
        return new CancelOrderCommand(UUID.fromString(orderId));
    }
}
