package org.icd4.commerce.application.provided.order.command;

import java.util.UUID;

/**
 * 결제 완료 처리 커맨드
 *
 * @param orderId 주문 ID
 * @param paymentId 결제 ID
 */
public record ConfirmPaymentCommand(String orderId, UUID paymentId) {

    public ConfirmPaymentCommand {
        if (orderId == null) {
            throw new IllegalArgumentException("orderId는 필수입니다.");
        }
        if (paymentId == null) {
            throw new IllegalArgumentException("paymentId는 필수입니다.");
        }
    }
}
