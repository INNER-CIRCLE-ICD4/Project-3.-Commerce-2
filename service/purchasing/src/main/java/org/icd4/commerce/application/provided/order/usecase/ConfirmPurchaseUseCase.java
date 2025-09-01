package org.icd4.commerce.application.provided.order.usecase;

import lombok.RequiredArgsConstructor;
import org.icd4.commerce.adapter.webapi.dto.order.response.OrderStatusResponse;
import org.icd4.commerce.application.provided.order.command.ConfirmPurchaseCommand;
import org.icd4.commerce.application.provided.order.support.OrderLoader;
import org.icd4.commerce.application.required.order.OrderRepositoryPort;
import org.icd4.commerce.domain.order.Order;
import org.icd4.commerce.domain.order.OrderId;
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

    public OrderStatusResponse confirmPurchase(String orderId) {
        Order order = orderLoader.findById(OrderId.from(orderId));
        order.confirmPurchase();
        Order savedOrder = orderRepository.save(order);
        return OrderStatusResponse.from(savedOrder);
    }
}
