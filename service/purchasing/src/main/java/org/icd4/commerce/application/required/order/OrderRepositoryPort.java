package org.icd4.commerce.application.required.order;

import org.icd4.commerce.domain.order.Order;
import org.icd4.commerce.domain.order.OrderId;

import java.util.Optional;

public interface OrderRepositoryPort {

    Order save(Order order);

    Optional<Order> findById(OrderId id);

    void deleteById(OrderId id);
}
