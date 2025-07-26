package org.icd4.commerce.application.provided.order.usecase;

import lombok.RequiredArgsConstructor;
import org.icd4.commerce.application.provided.order.command.CancelOrderCommand;
import org.icd4.commerce.application.required.order.OrderRepositoryPort;
import org.icd4.commerce.domain.order.Order;
import org.icd4.commerce.domain.order.OrderId;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CancelOrderUseCase {

    private final OrderRepositoryPort orderRepository;

    public void execute(CancelOrderCommand command) {
        // 1. 주문 조회
        Order order = orderRepository.findById(OrderId.from(String.valueOf(command.orderId())))
                .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다."));

        // 2. 도메인 로직 수행
        order.cancel();

        // 3. 저장
        orderRepository.save(order);
    }
}
