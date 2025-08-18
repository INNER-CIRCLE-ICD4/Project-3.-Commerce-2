package org.icd4.commerce.application.provided.order;

import org.icd4.commerce.application.provided.order.command.CreateOrderCommand;
import org.icd4.commerce.application.provided.order.usecase.CreateOrderUseCase;
import org.icd4.commerce.application.required.order.OrderRepositoryPort;
import org.icd4.commerce.domain.order.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateOrderUseCaseTest {

    @Mock
    private OrderRepositoryPort orderRepository;

    @InjectMocks
    private CreateOrderUseCase createOrderUseCase;

    private CreateOrderCommand command;

    @BeforeEach
    void setUp() {
        command = new CreateOrderCommand(
                "CUST-001",
                List.of(
                        new CreateOrderCommand.OrderItemCommand("1", 1000L, 2),
                        new CreateOrderCommand.OrderItemCommand("2", 500L, 1)
                ),
                "요청사항입니다",
                "WEB"
        );
    }

    @Test
    @DisplayName("주문 생성 성공")
    void createOrder_success() {
        // given
        Order dummyOrder = mock(Order.class);
        when(orderRepository.save(any(Order.class))).thenReturn(dummyOrder);

        // when
        Order result = createOrderUseCase.execute(command);

        // then
        assertThat(result).isNotNull();
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    @DisplayName("주문 항목이 비어 있으면 예외 발생")
    void createOrder_emptyItems_throwsException() {
        // when & then
        assertThatThrownBy(() ->
                new CreateOrderCommand(
                        "CUST-001",
                        List.of(),
                        "요청사항입니다",
                        "WEB"
                )
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("최소 1개의 주문 항목이 필요합니다.");
    }

}

