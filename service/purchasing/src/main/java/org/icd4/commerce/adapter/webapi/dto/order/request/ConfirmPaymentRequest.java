package org.icd4.commerce.adapter.webapi.dto.order.request;

import org.icd4.commerce.application.provided.order.command.ConfirmPaymentCommand;

import java.util.UUID;

public record ConfirmPaymentRequest(String paymentId) {
    public ConfirmPaymentCommand toCommand(String orderId) {
        return new ConfirmPaymentCommand(
                orderId,
                UUID.fromString(paymentId)
        );
    }
}

