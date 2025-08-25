package org.icd4.commerce.adapter.webapi.dto.order.request;

import org.icd4.commerce.application.provided.order.command.FailPaymentCommand;
import org.icd4.commerce.domain.order.OrderId;

public record FailPaymentRequest(String id) {
    public FailPaymentCommand toCommand(String id) {
        return new FailPaymentCommand(OrderId.from(id));
    }
}
