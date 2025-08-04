package org.icd4.commerce.application.provided.order.command;

/**
 * 환불 요청 커맨드
 *
 * @param orderId 주문 식별자
 */
public record RequestRefundCommand(Long orderId) {}
