package org.icd4.commerce.application.provided.order;

import org.icd4.commerce.application.provided.order.usecase.ConfirmPurchaseUseCase;
import org.icd4.commerce.application.required.order.OrderRepositoryPort;
import org.icd4.commerce.domain.order.Order;
import org.icd4.commerce.domain.order.OrderId;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@Disabled
@ExtendWith(MockitoExtension.class)
@DisplayName("ConfirmPurchaseUseCase 단위 테스트")
class ConfirmPurchaseUseCaseTest {

    @Mock
    private OrderRepositoryPort orderRepository;

    @InjectMocks
    private ConfirmPurchaseUseCase confirmPurchaseUseCase;

    @Test
    @DisplayName("구매 확정 성공")
    void confirmPurchase_success() {
        // given
        OrderId orderId = OrderId.generate();
        Order mockOrder = mock(Order.class);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(mockOrder));

        // when
        confirmPurchaseUseCase.confirmPurchase(orderId.value());

        // then
        verify(mockOrder).confirmPurchase();
        verify(orderRepository).save(mockOrder);
    }

    @Test
    @DisplayName("주문이 존재하지 않으면 예외 발생")
    void confirmPurchase_orderNotFound_throwsException() {
        // given
        OrderId orderId = OrderId.generate();

        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> confirmPurchaseUseCase.confirmPurchase(orderId.value()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("주문을 찾을 수 없습니다.");
    }
}
