package org.icd4.commerce.adapter.webapi.dto.order.request;

import org.icd4.commerce.application.provided.order.command.FailPaymentCommand;

public record FailPaymentRequest(String id) {
    public FailPaymentCommand toCommand(String id) {
        return new FailPaymentCommand(Long.parseLong(id));
    }
}
