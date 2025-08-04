package org.icd4.commerce.application.provided.order.command;

/**
 * 결제 실패 처리 커맨드
 *
 * @param orderId 주문 식별자
 */
//TODO:실패 사유 추가하면 좋을까?
public record FailPaymentCommand(Long orderId) {
}
