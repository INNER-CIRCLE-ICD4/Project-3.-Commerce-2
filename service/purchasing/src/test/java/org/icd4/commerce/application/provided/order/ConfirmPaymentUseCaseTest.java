package org.icd4.commerce.application.provided.order;

import org.icd4.commerce.application.provided.order.command.ConfirmPaymentCommand;
import org.icd4.commerce.application.provided.order.usecase.ConfirmPaymentUseCase;
import org.icd4.commerce.application.required.order.OrderRepositoryPort;
import org.icd4.commerce.domain.order.Order;
import org.icd4.commerce.domain.order.OrderId;
import org.icd4.commerce.domain.order.PaymentId;
import org.junit.jupiter.api.BeforeEach;
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
@DisplayName("ConfirmPaymentUseCase 단위 테스트")
class ConfirmPaymentUseCaseTest {

    @Mock
    private OrderRepositoryPort orderRepository;

    @InjectMocks
    private ConfirmPaymentUseCase confirmPaymentUseCase;

    private UUID orderId;
    private UUID paymentId;

    @BeforeEach
    void setUp() {
        orderId = UUID.randomUUID();
        paymentId = UUID.randomUUID();
    }

    @Test
    @DisplayName("결제 완료 처리 성공")
    void confirmPayment_success() {
        // given
        Order mockOrder = mock(Order.class);
        when(orderRepository.findById(new OrderId(orderId)))
                .thenReturn(Optional.of(mockOrder));

        ConfirmPaymentCommand command = new ConfirmPaymentCommand(orderId, paymentId);

        // when
        confirmPaymentUseCase.execute(command);

        // then
        verify(mockOrder).confirmPayment(new PaymentId(paymentId));
        verify(orderRepository).save(mockOrder);
    }

    @Test
    @DisplayName("주문이 존재하지 않으면 예외 발생")
    void confirmPayment_orderNotFound_throwsException() {
        // given
        when(orderRepository.findById(new OrderId(orderId)))
                .thenReturn(Optional.empty());

        ConfirmPaymentCommand command = new ConfirmPaymentCommand(orderId, paymentId);

        // when & then
        assertThatThrownBy(() -> confirmPaymentUseCase.execute(command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("해당 주문이 존재하지 않습니다");
    }
}
