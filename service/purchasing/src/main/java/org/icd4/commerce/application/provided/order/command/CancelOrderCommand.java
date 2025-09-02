package org.icd4.commerce.application.provided.order.command;

import org.icd4.commerce.domain.order.OrderId;

public record CancelOrderCommand(
        OrderId orderId
) {

}
