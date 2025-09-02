package org.icd4.commerce.application.provided.order.usecase;

import lombok.RequiredArgsConstructor;
import org.icd4.commerce.adapter.webapi.dto.order.response.OrderStatusResponse;
import org.icd4.commerce.application.provided.order.support.OrderLoader;
import org.icd4.commerce.application.required.order.OrderRepositoryPort;
import org.icd4.commerce.domain.order.Order;
import org.icd4.commerce.domain.order.OrderId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class GetOrderUseCase {
    private final OrderLoader orderLoader;

    public OrderStatusResponse getOrderStatus(String orderId) {
        Order order = orderLoader.findById(OrderId.from(orderId));
        return OrderStatusResponse.from(order);
    }
}
