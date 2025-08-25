package org.icd4.commerce.application.provided.order.usecase;

import lombok.RequiredArgsConstructor;
import org.icd4.commerce.application.provided.order.command.RequestRefundCommand;
import org.icd4.commerce.application.provided.order.support.OrderLoader;
import org.icd4.commerce.application.required.order.OrderRepositoryPort;
import org.icd4.commerce.domain.order.Order;
import org.icd4.commerce.domain.order.OrderId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class RequestRefundUseCase {

    private final OrderRepositoryPort orderRepository;
    private final OrderLoader orderLoader;

    public void requestRefund(RequestRefundCommand command) {
        Order order = orderLoader.loadOrThrow(command.orderId());
        order.requestRefund();
        orderRepository.save(order);
    }
}
