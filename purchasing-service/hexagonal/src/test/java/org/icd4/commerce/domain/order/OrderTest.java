package org.icd4.commerce.domain.order;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

class OrderTest {
    //주문 객체 생성용 헬퍼 메서드
    private Order createOrder(OrderStatus status, LocalDateTime completedAt) {
        Order order = Order.create(
                new CustomerId("test-customer"),
                List.of(new OrderItem(
                        new OrderItemId(UUID.randomUUID()),
                        new OrderId(UUID.randomUUID()),
                        new ProductId(1L),
                        "테스트상품",
                        10_000L, // unitPrice
                        3L,      // quantity
                        Map.of("색상", "빨강")
                        )
                ),
                "문앞에 놔주세요",
                "WEB"
        );

        //주문상태 설정(실제 코드에서는 setter 대신 리플렉션 사용 권장 안 함)
        if (status != OrderStatus.PENDING) {
            if (status == OrderStatus.PAID) order.confirmPayment(new PaymentId(UUID.randomUUID()));
            else if (status == OrderStatus.COMPLETED) {
                order.confirmPayment(new PaymentId(UUID.randomUUID()));
                order.confirmPurchase();
            } else if (status == OrderStatus.REFUND_IN_PROGRESS) {
                order.confirmPayment(new PaymentId(UUID.randomUUID()));
                order.confirmPurchase();
                order.requestRefund();
            }
        }
        //requestRefund_failsAfter7Days 테스트용 completedAt 값 강제 설정
        if (completedAt != null) {
            try {
                Field field = Order.class.getDeclaredField("completedAt");
                field.setAccessible(true);
                field.set(order, completedAt);
            } catch (Exception e) {
                throw new RuntimeException("completedAt 설정 실패", e);
            }
        }

        return order;
    }

    @Test
    @DisplayName("주문 생성 시 총 금액이 정상적으로 계산된다")
    void createOrder_calculatesTotalAmount() {
        OrderItem item1 = new OrderItem(
                new OrderItemId(UUID.randomUUID()),
                new OrderId(UUID.randomUUID()),
                new ProductId(1L),
                "테스트상품",
                10_000L,
                3L,
                Map.of("색상", "빨강")
        );
        OrderItem item2 = new OrderItem(
                new OrderItemId(UUID.randomUUID()),
                new OrderId(UUID.randomUUID()),
                new ProductId(1L),
                "테스트상품",
                10_000L,
                3L,
                Map.of("색상", "빨강")
        );

        Order order = Order.create(
                new CustomerId("c1"),
                List.of(item1, item2),
                "빠른 배송",
                "APP"
        );

        assertThat(order.getTotalAmount().getAmount()).isEqualTo(
                BigDecimal.valueOf(item1.getItemAmount() + item2.getItemAmount())
        );
    }

    @Test
    @DisplayName("결제 완료 처리 시 상태가 PAID로 변경된다")
    void confirmPayment_updatesStatus() {
        Order order = createOrder(OrderStatus.PENDING, null);

        order.confirmPayment(new PaymentId(UUID.randomUUID()));

        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.PAID);
    }

    @Test
    @DisplayName("결제 실패 상태에서 주문을 취소할 수 있다")
    void cancel_afterFailPayment() {
        Order order = createOrder(OrderStatus.PENDING, null);
        order.failPayment();

        order.cancel();

        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.CANCELED);
    }

    @Test
    @DisplayName("7일 초과된 주문은 환불 요청이 불가능하다")
    void requestRefund_failsAfter7Days() {
        Order order = createOrder(OrderStatus.COMPLETED, LocalDateTime.now().minusDays(8));

        assertThatThrownBy(order::requestRefund)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("7일 이내");
    }

    @Test
    @DisplayName("환불 요청 후 상태는 REFUND_IN_PROGRESS가 된다")
    void requestRefund_setsStatusCorrectly() {
        Order order = createOrder(OrderStatus.COMPLETED, LocalDateTime.now().minusDays(2));

        order.requestRefund();

        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.REFUND_IN_PROGRESS);
    }

    @Test
    @DisplayName("환불 완료 처리 시 상태는 REFUNDED가 된다")
    void refund_setsStatusToRefunded() {
        Order order = createOrder(OrderStatus.COMPLETED, LocalDateTime.now().minusDays(2));
        order.requestRefund();

        order.refund();

        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.REFUNDED);
    }

    @Test
    @DisplayName("주문 항목이 비어있으면 생성 시 예외가 발생한다")
    void createOrder_throwsException_whenOrderItemsEmpty() {
        assertThatThrownBy(() -> Order.create(
                new CustomerId("c1"),
                Collections.emptyList(),
                "메모",
                "APP"
        )).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("최소 1개");
    }

}
