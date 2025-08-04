package org.icd4.commerce.adapter.persistence;

import org.icd4.commerce.application.required.order.OrderRepositoryPort;
import org.icd4.commerce.domain.order.Order;
import org.icd4.commerce.domain.order.OrderId;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 *OrderRepositoryPort의 JPA 구현체.
 *
 * <p>헥사고날 아키텍처의 어댑터로서, 도메인 포트를 JPA Repository로 구현합니다.
 * 이를 통해 도메인 레이어가 JPA에 직접 의존하지 않도록 합니다.</p>
 */

@Component
@Transactional
public class OrderRepositoryAdapter implements OrderRepositoryPort {

    private final OrderRepository orderRepository;

    public OrderRepositoryAdapter(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    @Transactional
    public Order save(Order order){return orderRepository.save(order);};

    @Override
    public Optional<Order> findById(OrderId id){return orderRepository.findById(id);};
    /**
     * 특정 주문이 존재하는지 확인합니다.
     */
    @Override
    public boolean existsById(OrderId id){return orderRepository.existsById(id);};
    /**
     * 주문을 삭제합니다.
     */
    @Override
    public void deleteById(OrderId id){orderRepository.deleteById(id);};
}
