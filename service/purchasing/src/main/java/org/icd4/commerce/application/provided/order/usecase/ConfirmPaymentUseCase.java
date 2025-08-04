package org.icd4.commerce.application.provided.order.usecase;

import lombok.RequiredArgsConstructor;
import org.icd4.commerce.application.provided.order.command.ConfirmPaymentCommand;
import org.icd4.commerce.domain.order.Order;
import org.icd4.commerce.domain.order.OrderId;
import org.icd4.commerce.domain.order.PaymentId;
import org.icd4.commerce.application.required.order.OrderRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Service
@Transactional
public class ConfirmPaymentUseCase {

    private final OrderRepositoryPort orderRepository;

    public void execute(ConfirmPaymentCommand command) {
        OrderId orderId = new OrderId(command.orderId());
        PaymentId paymentId = new PaymentId(command.paymentId());

        Optional<Order> orderOptional = orderRepository.findById(orderId);

        if (orderOptional.isEmpty()) {
            throw new IllegalArgumentException("해당 주문이 존재하지 않습니다: " + command.orderId());
        }

        Order order = orderOptional.get();
        order.confirmPayment(paymentId);

        orderRepository.save(order);
    }
}
