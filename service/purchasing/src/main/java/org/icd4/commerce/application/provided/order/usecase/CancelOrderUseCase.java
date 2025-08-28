package org.icd4.commerce.application.provided.order.usecase;

import lombok.RequiredArgsConstructor;
import org.icd4.commerce.adapter.webapi.dto.order.response.OrderStatusResponse;
import org.icd4.commerce.application.provided.order.command.CancelOrderCommand;
import org.icd4.commerce.application.provided.order.support.OrderLoader;
import org.icd4.commerce.application.required.order.OrderRepositoryPort;
import org.icd4.commerce.domain.order.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class CancelOrderUseCase {

    private final OrderRepositoryPort orderRepository;
    private final OrderLoader orderLoader;

    public OrderStatusResponse cancelOrder(CancelOrderCommand command) {
        Order order = orderLoader.findById(command.orderId());
        order.cancel();
        return OrderStatusResponse.from(orderRepository.save(order));
    }
}