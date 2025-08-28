package org.icd4.commerce.application.provided.order.command;

import org.icd4.commerce.domain.order.OrderId;

import java.util.UUID;

public record CancelOrderCommand(
        OrderId orderId
) {

}
