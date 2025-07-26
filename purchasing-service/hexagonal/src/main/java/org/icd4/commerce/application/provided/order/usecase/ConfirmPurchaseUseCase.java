package org.icd4.commerce.application.provided.order.usecase;

import lombok.RequiredArgsConstructor;
import org.icd4.commerce.application.provided.order.command.ConfirmPurchaseCommand;
import org.icd4.commerce.application.required.order.OrderRepositoryPort;
import org.icd4.commerce.domain.order.Order;
import org.icd4.commerce.domain.order.OrderId;
import org.springframework.stereotype.Service;

/**
 * 구매 확정 유스케이스
 */
@RequiredArgsConstructor
@Service
public class ConfirmPurchaseUseCase {

    private final OrderRepositoryPort orderRepository;

    public void execute(ConfirmPurchaseCommand command) {
        Order order = orderRepository.findById(OrderId.from(String.valueOf(command.orderId())))
                .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다."));

        order.confirmPurchase();

        orderRepository.save(order);
    }
}
