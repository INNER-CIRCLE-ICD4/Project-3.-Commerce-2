package org.icd4.commerce.domain.order;

import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * 주문 애그리거트 루트입니다.
 *
 * <p>주문 생성, 상태 전이, 총 금액 계산 등의 핵심 도메인 로직을 포함합니다.</p>
 */
@Getter
public class Order {
    private final OrderId orderId;
    private final CustomerId customerId;
    private final List<OrderItem> orderItems;
    private OrderStatus orderStatus;
    private Money totalAmount;
    private final String orderMessage;
    private PaymentId paymentId;
    private final String orderChannel;
    private final LocalDateTime createdAt;
    private LocalDateTime lastModifiedAt;
    private LocalDateTime completedAt;

    /**
     * 생성자 - 외부에서 직접 호출하지 않고, 정적 팩토리 메서드를 통해 사용합니다.
     */
    private Order(
            OrderId orderId,
            CustomerId customerId,
            List<OrderItem> orderItems,
            String orderMessage,
            String orderChannel
    ) {
        this.orderId = Objects.requireNonNull(orderId, "orderId는 필수입니다.");
        this.customerId = Objects.requireNonNull(customerId, "customerId는 필수입니다.");
        this.orderItems = Objects.requireNonNull(orderItems, "orderItems는 필수입니다.");

        if (orderItems.isEmpty()) {
            throw new IllegalArgumentException("주문 항목은 최소 1개 이상이어야 합니다.");
        }

        this.orderMessage = orderMessage;
        this.orderChannel = orderChannel;
        this.orderStatus = OrderStatus.PENDING;
        this.createdAt = LocalDateTime.now();
        this.lastModifiedAt = this.createdAt;
        this.totalAmount = calculateTotal(); // 주문 총액 계산
    }

    /**
     * 주문 생성 팩토리 메서드
     *
     * @param customerId    주문자 ID
     * @param orderItems    주문 상품 목록
     * @param orderMessage  고객 메시지
     * @param orderChannel  주문 채널
     * @return 생성된 주문 객체
     */
    public static Order create(
            CustomerId customerId,
            List<OrderItem> orderItems,
            String orderMessage,
            String orderChannel
    ) {
        return new Order(OrderId.generate(), customerId, orderItems, orderMessage, orderChannel);
    }

    public static Order restore(
            OrderId orderId,
            CustomerId customerId,
            List<OrderItem> orderItems,
            OrderStatus orderStatus,
            Money totalAmount,
            String orderMessage,
            PaymentId paymentId,
            String orderChannel,
            LocalDateTime createdAt,
            LocalDateTime lastModifiedAt,
            LocalDateTime completedAt
    ) {
        Order order = new Order(orderId, customerId, orderItems, orderMessage, orderChannel);
        order.orderStatus = orderStatus;
        order.totalAmount = totalAmount;
        order.paymentId = paymentId;
        order.lastModifiedAt = lastModifiedAt;
        order.completedAt = completedAt;
        return order;
    }

    /**
     * 총 주문 금액 계산
     */
    public Money calculateTotal() {
        return orderItems.stream()
                .map(item -> Money.of(BigDecimal.valueOf(item.getItemAmount())))
                .reduce(Money.ZERO, Money::add);
    }

    /**
     * 결제 완료 처리 (PENDING → PAID)
     */
    public void confirmPayment(PaymentId paymentId) {
        if (this.orderStatus != OrderStatus.PENDING) {
            throw new IllegalStateException("결제는 PENDING 상태에서만 가능합니다.");
        }
        this.orderStatus = OrderStatus.PAID;
        this.paymentId = paymentId;
        this.lastModifiedAt = LocalDateTime.now();

        //TODO: [재고 차감 처리 요청]구현 필요
    }

    /**
     * 결제 실패 처리 (PENDING → PAYMENT_FAILED)
     */
    public void failPayment() {
        if (this.orderStatus != OrderStatus.PENDING) {
            throw new IllegalStateException("결제 실패는 PENDING 상태에서만 가능합니다.");
        }
        this.orderStatus = OrderStatus.PAYMENT_FAILED;
        this.lastModifiedAt = LocalDateTime.now();
    }

    /**
     * 주문 취소 처리 (PAYMENT_FAILED / PAID / COMPLETED → CANCELED)
     */
    public void cancel() {
        if (!(this.orderStatus == OrderStatus.PAID || this.orderStatus == OrderStatus.COMPLETED || this.orderStatus == OrderStatus.PAYMENT_FAILED)) {
            throw new IllegalStateException("주문은 결제 완료/구매 확정/결제 실패 상태에서만 취소할 수 있습니다.");
        }
        this.orderStatus = OrderStatus.CANCELED;
        this.lastModifiedAt = LocalDateTime.now();
    }

    /**
     * 재고 부족 처리
     */
    public void markOutOfStock() {
        if (this.orderStatus != OrderStatus.PENDING && this.orderStatus != OrderStatus.PAID) {
            throw new IllegalStateException("재고 부족 처리는 PENDING 또는 PAID 상태에서만 가능합니다.");
        }
        this.orderStatus = OrderStatus.OUT_OF_STOCK;
        this.lastModifiedAt = LocalDateTime.now();
    }

    /**
     * 구매 확정 처리 (PAID → COMPLETED)
     */
    public void confirmPurchase() {
        if (this.orderStatus != OrderStatus.PAID) {
            throw new IllegalStateException("구매 확정은 PAID 상태에서만 가능합니다.");
        }
        this.orderStatus = OrderStatus.COMPLETED;
        this.completedAt = LocalDateTime.now();
        this.lastModifiedAt = LocalDateTime.now();
    }

    /**
     * 환불 요청 처리 (COMPLETED → REFUND_IN_PROGRESS)
     */
    public void requestRefund() {
        if (this.orderStatus != OrderStatus.COMPLETED) {
            throw new IllegalStateException("환불 요청은 COMPLETED 상태에서만 가능합니다.");
        }
        if (this.completedAt == null || this.completedAt.isBefore(LocalDateTime.now().minusDays(7))) {
            throw new IllegalStateException("환불 요청은 주문 완료일로부터 7일 이내에만 가능합니다.");
        }
        this.orderStatus = OrderStatus.REFUND_IN_PROGRESS;
        this.lastModifiedAt = LocalDateTime.now();
    }

    /**
     * 환불 완료 처리 (REFUND_IN_PROGRESS → REFUNDED)
     */
    public void refund() {
        if (this.orderStatus != OrderStatus.REFUND_IN_PROGRESS) {
            throw new IllegalStateException("환불 완료는 REFUND_IN_PROGRESS 상태에서만 가능합니다.");
        }
        this.orderStatus = OrderStatus.REFUNDED;
        this.lastModifiedAt = LocalDateTime.now();
    }

}
