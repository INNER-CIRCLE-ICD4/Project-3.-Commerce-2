package org.icd4.commerce.application.provided.order;

import org.icd4.commerce.application.provided.order.command.ConfirmPurchaseCommand;
import org.icd4.commerce.application.provided.order.usecase.ConfirmPurchaseUseCase;
import org.icd4.commerce.application.required.order.OrderRepositoryPort;
import org.icd4.commerce.domain.order.Order;
import org.icd4.commerce.domain.order.OrderId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

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
        UUID uuid = UUID.randomUUID();
        OrderId orderId = new OrderId(uuid);
        Order mockOrder = mock(Order.class);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(mockOrder));
        ConfirmPurchaseCommand command = new ConfirmPurchaseCommand(uuid);

        // when
        confirmPurchaseUseCase.execute(command);

        // then
        verify(mockOrder).confirmPurchase();
        verify(orderRepository).save(mockOrder);
    }

    @Test
    @DisplayName("주문이 존재하지 않으면 예외 발생")
    void confirmPurchase_orderNotFound_throwsException() {
        // given
        UUID uuid = UUID.randomUUID();
        OrderId orderId = new OrderId(uuid);

        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());
        ConfirmPurchaseCommand command = new ConfirmPurchaseCommand(uuid);

        // when & then
        assertThatThrownBy(() -> confirmPurchaseUseCase.execute(command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("주문을 찾을 수 없습니다.");
    }
}
