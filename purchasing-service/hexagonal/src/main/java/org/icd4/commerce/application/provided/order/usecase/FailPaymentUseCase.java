package org.icd4.commerce.application.provided.order.usecase;


import lombok.RequiredArgsConstructor;
import org.icd4.commerce.application.provided.order.command.FailPaymentCommand;
import org.icd4.commerce.application.required.order.OrderRepositoryPort;
import org.icd4.commerce.domain.order.Order;
import org.icd4.commerce.domain.order.OrderId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 결제 실패 처리 유스케이스
 */
@RequiredArgsConstructor
@Service
@Transactional
public class FailPaymentUseCase {

    private final OrderRepositoryPort orderRepository;

    public void execute(FailPaymentCommand command) {
        Order order = orderRepository.findById(OrderId.from(String.valueOf(command.orderId())))
                .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다."));

        order.failPayment();

        orderRepository.save(order);
    }
}
