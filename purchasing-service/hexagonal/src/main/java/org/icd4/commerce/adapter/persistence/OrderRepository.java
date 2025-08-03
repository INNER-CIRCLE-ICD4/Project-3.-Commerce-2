package org.icd4.commerce.adapter.persistence;

import org.icd4.commerce.adapter.persistence.mapper.OrderEntityMapper;
import org.icd4.commerce.domain.order.Order;
import org.icd4.commerce.domain.order.OrderId;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 주문 레포지토리 구현체
 *
 * <p>JPA+Mapper 조립하여 실제 저장소 역할</p>
 */
@Repository
public class OrderRepository {
    private final OrderJpaRepository jpaRepository;
    private final OrderEntityMapper mapper;

    public OrderRepository(OrderJpaRepository jpaRepository, OrderEntityMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    public Order save(Order order) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(order)));
    }

    public Optional<Order> findById(OrderId id) {
        return jpaRepository.findById(String.valueOf(id.value())).map(mapper::toDomain);
    }

    public boolean existsById(OrderId id) {
        return jpaRepository.existsById(String.valueOf(id.value()));
    }

    public void deleteById(OrderId id) {
        jpaRepository.deleteById(String.valueOf(id.value()));
    }
}
