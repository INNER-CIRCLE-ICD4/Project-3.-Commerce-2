package org.icd4.commerce.application.provided.order.command;

import java.io.Serializable;
import java.util.UUID;

/**
 * 구매 확정 커맨드
 *
 * @param orderId 구매 확정할 주문 ID
 */
public record ConfirmPurchaseCommand(UUID orderId) implements Serializable {
    public ConfirmPurchaseCommand {
        if (orderId == null) {
            throw new IllegalArgumentException("orderId must not be null");
        }
    }
}
