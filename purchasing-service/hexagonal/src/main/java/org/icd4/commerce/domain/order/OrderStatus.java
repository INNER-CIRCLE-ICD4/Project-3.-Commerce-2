package org.icd4.commerce.domain.order;

/**
 * 주문 상태
 */
public enum OrderStatus {

    /** 주문 생성됨 */
    PENDING,

    /** 결제 완료됨 */
    PAID,

    /** 상품 수령 및 구매 확정 */
    COMPLETED,

    /** 관리자에 의해 주문 취소됨 */
    CANCELED,

    /** 결제 실패 - 사용자에게 노출되지 않음 */
    PAYMENT_FAILED,

    /** 재고 부족으로 처리 실패 - 사용자에게 노출되지 않음 */
    OUT_OF_STOCK,

    /** 환불 진행 중 */
    REFUND_IN_PROGRESS,

    /** 환불 완료 */
    REFUNDED
}
