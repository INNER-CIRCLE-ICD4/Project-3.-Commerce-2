package org.icd4.commerce.application.required.order;

import org.icd4.commerce.domain.order.Order;
import org.icd4.commerce.domain.order.OrderId;

import java.util.Optional;

public interface OrderRepositoryPort {
    /**
     * 주문을 저장합니다.
     */
    Order save(Order order);
    /**
     * 특정 주문ID로 주문을 조회합니다.
     */
    Optional<Order> findById(OrderId id);
    /**
     * 특정 주문이 존재하는지 확인합니다.
     */
    boolean existsById(OrderId id);
    /**
     * 주문을 삭제합니다.
     */
    void deleteById(OrderId id);
}
