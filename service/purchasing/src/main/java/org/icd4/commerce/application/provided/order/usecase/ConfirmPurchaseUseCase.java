package org.icd4.commerce.application.provided.order.usecase;

import lombok.RequiredArgsConstructor;
import org.icd4.commerce.application.provided.order.command.ConfirmPurchaseCommand;
import org.icd4.commerce.application.provided.order.support.OrderLoader;
import org.icd4.commerce.application.required.order.OrderRepositoryPort;
import org.icd4.commerce.domain.order.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 구매 확정 유스케이스
 */
@RequiredArgsConstructor
@Service
@Transactional
public class ConfirmPurchaseUseCase {

    private final OrderRepositoryPort orderRepository;
    private final OrderLoader orderLoader;

    public void confirmPurchase(ConfirmPurchaseCommand command) {
        Order order = orderLoader.findById(command.orderId());
        order.confirmPurchase();
        orderRepository.save(order);
    }
}
