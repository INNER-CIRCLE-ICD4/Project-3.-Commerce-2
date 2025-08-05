package org.icd4.commerce.application.provided.order.usecase;

import lombok.RequiredArgsConstructor;
import org.icd4.commerce.application.provided.cart.InsufficientStockException;
import org.icd4.commerce.application.provided.order.command.CreateOrderCommand;
import org.icd4.commerce.application.required.order.OrderRepositoryPort;
import org.icd4.commerce.application.required.common.InventoryChecker;
import org.icd4.commerce.domain.common.ProductId;
import org.icd4.commerce.domain.order.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
@Transactional
public class CreateOrderUseCase {
    private final OrderRepositoryPort orderRepository;
    private final InventoryChecker inventoryChecker;

    public Order execute(CreateOrderCommand command) {
        //1.재고 확인
        for (var item : command.items()) {
            ProductId productId = new ProductId(item.productId().toString());
            int available = inventoryChecker.getAvailableStock(productId);

            if (available < item.quantity()) {
                throw new InsufficientStockException(productId, available, (int) item.quantity());
            }
        }

        //2.주문 항목 생성
        List<OrderItem> orderItems = command.items().stream()
                .map(item -> new OrderItem(
                        OrderItemId.generate(),
                        OrderId.generate(),
                        new ProductId(item.productId().toString()),
                        "테스트상품명",
                        item.unitPrice(),
                        item.quantity(),
                        Map.of()
                ))
                .toList();

        Order order = Order.create(
                new CustomerId(command.customerId()),
                orderItems,
                command.orderMessage(),
                command.orderChannel()
        );

        return orderRepository.save(order);
    }
}
