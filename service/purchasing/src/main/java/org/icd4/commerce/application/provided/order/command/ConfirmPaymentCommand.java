package org.icd4.commerce.application.provided.order.command;

import org.icd4.commerce.domain.order.OrderId;
import org.icd4.commerce.domain.order.PaymentId;

import java.util.UUID;

/**
 * 결제 완료 처리 커맨드
 *
 * @param orderId 주문 ID
 * @param paymentId 결제 ID
 */
public record ConfirmPaymentCommand(OrderId orderId, PaymentId paymentId) {

    public ConfirmPaymentCommand {
        if (orderId == null) {
            throw new IllegalArgumentException("orderId는 필수입니다.");
        }
        if (paymentId == null) {
            throw new IllegalArgumentException("paymentId는 필수입니다.");
        }
    }
}
