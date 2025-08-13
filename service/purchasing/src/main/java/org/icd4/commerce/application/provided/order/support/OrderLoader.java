package org.icd4.commerce.application.provided.order.support;

import org.icd4.commerce.application.required.order.OrderRepositoryPort;

public class OrderLoader {

    private final OrderRepositoryPort orderRepository;

    public OrderLoader(OrderRepositoryPort orderRepository) {
        this.orderRepository = orderRepository;
    }
}
