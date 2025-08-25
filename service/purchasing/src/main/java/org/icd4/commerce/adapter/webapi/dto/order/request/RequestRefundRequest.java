package org.icd4.commerce.adapter.webapi.dto.order.request;

import org.icd4.commerce.application.provided.order.command.RequestRefundCommand;
import org.icd4.commerce.domain.order.OrderId;

public record RequestRefundRequest() {
    public RequestRefundCommand toCommand(String id) {
        return new RequestRefundCommand(OrderId.from(id));
    }
}
