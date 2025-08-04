package org.icd4.commerce.adapter.webapi.dto.order.request;

import org.icd4.commerce.application.provided.order.command.RequestRefundCommand;

public record RequestRefundRequest() {
    public RequestRefundCommand toCommand(String id) {
        return new RequestRefundCommand(Long.parseLong(id));
    }
}
