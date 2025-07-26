package org.icd4.commerce.application.provided.order.usecase;

import lombok.RequiredArgsConstructor;
import org.icd4.commerce.application.provided.order.command.CreateOrderCommand;
import org.icd4.commerce.application.required.order.OrderRepositoryPort;
import org.icd4.commerce.domain.order.*;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class CreateOrderUseCase {
    private final OrderRepositoryPort orderRepository;

    public Order execute(CreateOrderCommand command) {
        // 주문 항목 생성
        List<OrderItem> orderItems = command.items().stream()
                .map(item -> new OrderItem(
                        OrderItemId.generate(),
                        null,
                        new ProductId(item.productId()),
                        "",
                        item.unitPrice(),
                        item.quantity(),
                        null
                ))
                .toList();

        // 도메인 create 메서드 사용
        Order order = Order.create(
                new CustomerId(command.customerId()),
                orderItems,
                command.orderMessage(),
                command.orderChannel()
        );

        return orderRepository.save(order);
    }
}
