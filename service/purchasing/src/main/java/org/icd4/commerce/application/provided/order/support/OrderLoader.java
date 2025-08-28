package org.icd4.commerce.application.provided.order.support;

import lombok.RequiredArgsConstructor;
import org.icd4.commerce.application.required.order.OrderRepositoryPort;
import org.icd4.commerce.domain.order.Order;
import org.icd4.commerce.domain.order.OrderId;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderLoader {

    private final OrderRepositoryPort orderRepository;

    public Order findById(OrderId orderId) {
       return orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다."));
    }
}
