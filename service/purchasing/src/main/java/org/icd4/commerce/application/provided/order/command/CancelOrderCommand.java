package org.icd4.commerce.application.provided.order.command;

import java.util.UUID;

public record CancelOrderCommand(
        String orderId
) {

}
