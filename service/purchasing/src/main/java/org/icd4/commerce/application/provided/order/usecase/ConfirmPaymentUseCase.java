package org.icd4.commerce.application.provided.order.usecase;

import lombok.RequiredArgsConstructor;
import org.icd4.commerce.application.provided.order.command.ConfirmPaymentCommand;
import org.icd4.commerce.application.provided.order.support.OrderLoader;
import org.icd4.commerce.application.required.order.OrderRepositoryPort;
import org.icd4.commerce.domain.order.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class ConfirmPaymentUseCase {

    private final OrderRepositoryPort orderRepository;
    private final OrderLoader orderLoader;

    public void confirmPayment(ConfirmPaymentCommand command) {
        Order order = orderLoader.loadOrThrow(command.orderId());
        order.confirmPayment(command.paymentId());
        orderRepository.save(order);
    }
}
