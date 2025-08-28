package org.icd4.commerce.application.provided.order;

import org.icd4.commerce.application.provided.common.ProductDetailsProvider;
import org.icd4.commerce.application.provided.order.command.CreateOrderCommand;
import org.icd4.commerce.application.provided.order.usecase.CreateOrderUseCase;
import org.icd4.commerce.application.required.common.InventoryChecker;
import org.icd4.commerce.application.required.order.OrderRepositoryPort;
import org.icd4.commerce.domain.common.ProductId;
import org.icd4.commerce.domain.common.StockKeepingUnit;
import org.icd4.commerce.domain.order.Order;
import org.icd4.commerce.domain.order.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.icd4.commerce.application.provided.common.ProductDetailsProvider.ProductDetails;
import static org.icd4.commerce.application.required.common.InventoryChecker.AvailableStock;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateOrderUseCaseTest {

    @Mock
    private OrderRepositoryPort orderRepository;
    @Mock
    private ProductDetailsProvider productDetailsProvider;
    @Mock
    private InventoryChecker inventoryChecker;

    @InjectMocks
    private CreateOrderUseCase createOrderUseCase;

    private CreateOrderCommand command;
    private ProductDetails mockProduct1;
    private ProductDetails mockProduct2;
    private AvailableStock sufficientStock;


    @BeforeEach
    void setUp() {
        // Given: 유효한 주문 생성 명령
        command = new CreateOrderCommand(
                "CUST-001",
                List.of(
                        new CreateOrderCommand.OrderItemCommand("PROD-001", "SKU-001", 29900L, 2),
                        new CreateOrderCommand.OrderItemCommand("PROD-002", "SKU-002", 15000L, 1)
                ),
                "배송 전 연락 부탁드립니다.",
                "WEB"
        );

        // Mock 상품 정보
        mockProduct1 = new ProductDetails("상품1", new BigDecimal("29900"), true);
        mockProduct2 = new ProductDetails("상품2", new BigDecimal("15000"), true);

        // 충분한 재고
        sufficientStock = new AvailableStock(100);
    }


    @Test
    @DisplayName("주문 생성 성공")
    void createOrder_success() {
        // Given
        when(productDetailsProvider.getProductInfo(eq(ProductId.of("PROD-001")), eq(StockKeepingUnit.of("SKU-001"))))
                .thenReturn(mockProduct1);
        when(productDetailsProvider.getProductInfo(eq(ProductId.of("PROD-002")), eq(StockKeepingUnit.of("SKU-002"))))
                .thenReturn(mockProduct2);

        when(inventoryChecker.getAvailableStock(eq(StockKeepingUnit.of("SKU-001"))))
                .thenReturn(sufficientStock);
        when(inventoryChecker.getAvailableStock(eq(StockKeepingUnit.of("SKU-002"))))
                .thenReturn(sufficientStock);

        // ArgumentCaptor로 실제 저장되는 주문 객체 캡처
        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        when(orderRepository.save(orderCaptor.capture())).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Order result = createOrderUseCase.createOrder2(command);

        // Then
        assertThat(result).isNotNull();

        // 캡처된 주문 객체 검증
        Order capturedOrder = orderCaptor.getValue();
        assertThat(capturedOrder.getCustomerId().value()).isEqualTo("CUST-001");
        assertThat(capturedOrder.getOrderStatus()).isEqualTo(OrderStatus.PENDING);
        assertThat(capturedOrder.getOrderMessage()).isEqualTo("배송 전 연락 부탁드립니다.");
        assertThat(capturedOrder.getOrderChannel()).isEqualTo("WEB");

        // 총 금액 검증: (29900 * 2) + (15000 * 1) = 74800
        assertThat(capturedOrder.getTotalAmount().getAmount()).isEqualByComparingTo(new BigDecimal("74800"));

        // 각 서비스 호출 검증
        verify(productDetailsProvider).getProductInfo(eq(ProductId.of("PROD-001")), eq(StockKeepingUnit.of("SKU-001")));
        verify(productDetailsProvider).getProductInfo(eq(ProductId.of("PROD-002")), eq(StockKeepingUnit.of("SKU-002")));
        verify(inventoryChecker).getAvailableStock(eq(StockKeepingUnit.of("SKU-001")));
        verify(inventoryChecker).getAvailableStock(eq(StockKeepingUnit.of("SKU-002")));
        verify(orderRepository).save(any(Order.class));

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

