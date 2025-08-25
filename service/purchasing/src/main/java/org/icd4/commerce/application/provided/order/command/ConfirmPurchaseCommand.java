package org.icd4.commerce.application.provided.order.command;

import org.icd4.commerce.domain.order.OrderId;

import java.io.Serializable;

/**
 * 구매 확정 커맨드
 *
 * @param orderId 구매 확정할 주문 ID
 */
public record ConfirmPurchaseCommand(OrderId orderId){
    public ConfirmPurchaseCommand {
        if (orderId == null) {
            throw new IllegalArgumentException("orderId must not be null");
        }
    }
}
