package org.icd4.commerce.adapter.persistence;

import org.icd4.commerce.adapter.persistence.entity.CartJpaEntity;
import org.icd4.commerce.adapter.persistence.entity.OrderJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA 레포지토리 인터페이스.
 *
 * <p>OrderJpaEntity에 대한 데이터베이스 접근을 담당합니다.</p>
 *
 * @author Jooeun
 * @since 1.0
 */
@Repository
public interface OrderJpaRepository extends JpaRepository<OrderJpaEntity, String> {

}
