package org.icd4.commerce.adapter.persistence;

import org.icd4.commerce.adapter.persistence.entity.CartJpaEntity;
import org.icd4.commerce.adapter.persistence.mapper.CartEntityMapper;
import org.icd4.commerce.domain.cart.Cart;
import org.icd4.commerce.domain.cart.CartId;
import org.icd4.commerce.domain.cart.CustomerId;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 장바구니 레포지토리 구현체.
 * 
 * <p>도메인 모델과 JPA 엔티티 간의 변환을 처리하며,
 * 영속성 관리를 담당합니다.</p>
 * 
 * @author Jooeun
 * @since 1.0
 */
@Repository
public class CartRepository {
    
    private final CartJpaRepository jpaRepository;
    private final CartEntityMapper mapper;
    
    public CartRepository(CartJpaRepository jpaRepository, CartEntityMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }
    
    /**
     * 장바구니를 저장합니다.
     * 
     * @param cart 저장할 장바구니
     * @return 저장된 장바구니
     */
    public Cart save(Cart cart) {
        CartJpaEntity entity = mapper.toEntity(cart);
        CartJpaEntity savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }
    
    /**
     * ID로 장바구니를 조회합니다.
     * 
     * @param cartId 장바구니 ID
     * @return 장바구니 (없으면 Optional.empty())
     */
    public Optional<Cart> findById(CartId cartId) {
        return jpaRepository.findById(cartId.value())
            .map(mapper::toDomain);
    }
    
    /**
     * 특정 고객의 활성 장바구니를 조회합니다.
     * 
     * @param customerId 고객 ID
     * @return 장바구니 (없으면 Optional.empty())
     */
    public Optional<Cart> findActiveCartByCustomerId(CustomerId customerId) {
        return jpaRepository.findActiveCartByCustomerId(customerId.value())
            .map(mapper::toDomain);
    }
    
    /**
     * 특정 고객의 모든 장바구니를 조회합니다.
     * 
     * @param customerId 고객 ID
     * @return 장바구니 목록
     */
    public List<Cart> findByCustomerId(CustomerId customerId) {
        return jpaRepository.findByCustomerId(customerId.value()).stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }
    
    /**
     * 만료된 장바구니들을 조회합니다.
     * 
     * @param expiryDate 만료 기준 시간
     * @return 만료된 장바구니 목록
     */
    public List<Cart> findExpiredCarts(LocalDateTime expiryDate) {
        return jpaRepository.findExpiredCarts(expiryDate).stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }
    
    /**
     * 현재 시점 기준으로 만료된 장바구니를 조회합니다.
     * 90일간 수정이 없는 장바구니를 만료로 처리합니다.
     * 
     * @return 만료된 장바구니 목록
     */
    public List<Cart> findCurrentlyExpiredCarts() {
        return findExpiredCarts(LocalDateTime.now().minusDays(90));
    }
    
    /**
     * 특정 기간 동안 전환된 장바구니 수를 조회합니다.
     * 
     * @param startDate 시작 일시
     * @param endDate 종료 일시
     * @return 전환된 장바구니 수
     */
    public long countConvertedCartsBetween(LocalDateTime startDate, LocalDateTime endDate) {
        return jpaRepository.countConvertedCartsBetween(startDate, endDate);
    }
    
    /**
     * 장바구니와 관련된 모든 아이템을 함께 조회합니다.
     * N+1 문제를 방지하기 위한 fetch join 사용.
     * 
     * @param cartId 장바구니 ID
     * @return 아이템과 함께 로드된 장바구니
     */
    public Optional<Cart> findByIdWithItems(CartId cartId) {
        return jpaRepository.findByIdWithItems(cartId.value())
            .map(mapper::toDomain);
    }
    
    /**
     * ID로 장바구니를 삭제합니다.
     * 
     * @param cartId 삭제할 장바구니 ID
     */
    public void deleteById(CartId cartId) {
        jpaRepository.deleteById(cartId.value());
    }
    
    /**
     * 장바구니 존재 여부를 확인합니다.
     * 
     * @param cartId 확인할 장바구니 ID
     * @return 존재 여부
     */
    public boolean existsById(CartId cartId) {
        return jpaRepository.existsById(cartId.value());
    }
}