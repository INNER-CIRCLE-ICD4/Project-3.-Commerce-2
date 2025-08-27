package org.icd4.commerce.domain.order;

import org.icd4.commerce.domain.common.ProductId;
import org.junit.jupiter.api.*;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OrderItemTest {

    @Test
    @DisplayName("unitPrice와 quantity가 유효한 값일 때 정상적으로 계산된다.")
    void calculateItemAmount_returnsCorrectTotal() {
        // given
        OrderItem item = new OrderItem(
                new OrderItemId("1"),
                new OrderId("1"),
                new ProductId("1"),
                "테스트상품",
                10_000L, // unitPrice
                3,      // quantity
                Map.of("색상", "빨강")
        );

        // when
        long total = item.calculateItemAmount();

        // then
        assertThat(total).isEqualTo(30_000L);
    }

    @Test
    @DisplayName("상품명이 null이면 예외가 발생한다.")
    void constructor_throwException_whenProductNameIsNull() {
        //given: productName을 null로 처리
        //when&then: 주문 생성 시 예외 발생 확인
        assertThatThrownBy(() -> new OrderItem(
                new OrderItemId("1"),
                new OrderId("1"),
                new ProductId("1"),
                null,
                10000L,
                1,
                Map.of("색상", "빨강")
        )).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("상품명은 비어 있을 수 없습니다.");

    }

    @Test
    @DisplayName("상품명이 빈 값이면 예외가 발생한다.")
    void constructor_throwException_whenProductNameIsBlank() {
        //given: productName을 null로 처리
        //when&then: 주문 생성 시 예외 발생 확인
        assertThatThrownBy(() -> new OrderItem(
                new OrderItemId("1"),
                new OrderId("1"),
                new ProductId("1"),
                " ",
                10000L,
                1,
                Map.of("색상", "빨강")
        )).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("상품명은 비어 있을 수 없습니다.");

    }

    @Test
    @DisplayName("상품 가격이 음수면 예외가 발생한다.")
    void constructor_throwException_whenUnitPriceIsNegative() {
        //given: unitPrice가 음수
        //when&then: 주문 생성 시 예외 발생 확인
        assertThatThrownBy(() -> new OrderItem(
                new OrderItemId("1"),
                new OrderId("1"),
                new ProductId("1"),
                "테스트상품",
                -10000L,
                1,
                Map.of("색상", "빨강")
        )).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("상품 가격은 0 이상이어야 합니다.");

    }

    @Test
    @DisplayName("상품 수량이 0이거나 음수면 예외가 발생한다.")
    void constructor_throwException_whenProductQuantityIsLessThanOne() {
        //given: quantity가 0
        //when&then: 주문 생성 시 예외 발생 확인
        assertThatThrownBy(() -> new OrderItem(
                new OrderItemId("1"),
                new OrderId("1"),
                new ProductId("1"),
                "테스트상품",
                10000L,
                0,
                Map.of("색상", "빨강")
        )).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("상품 수량은 1개 이상이어야 합니다.");

    }
}

