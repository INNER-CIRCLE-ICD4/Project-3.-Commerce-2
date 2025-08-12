package org.icd4.commerce.application.required.cart;

import org.icd4.commerce.domain.cart.Cart;
import org.icd4.commerce.domain.cart.CartId;
import org.icd4.commerce.domain.cart.CustomerId;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 장바구니 저장소 포트 인터페이스.
 * 
 * <p>헥사고날 아키텍처의 출력 포트로, 도메인 레이어가 
 * 영속성 레이어에 대한 의존성 없이 데이터 접근이 가능하도록 합니다.</p>
 * 
 * <p>구현체는 어댑터 레이어에서 제공하며, 
 * JPA, MongoDB, Redis 등 다양한 저장소로 구현할 수 있습니다.</p>
 * 
 * @author Jooeun
 * @since 1.0
 */
@Repository
public interface CartRepositoryPort {
    
    /**
     * 장바구니를 저장합니다.
     * 
     * @param cart 저장할 장바구니
     * @return 저장된 장바구니
     */
    Cart save(Cart cart);
    
    /**
     * ID로 장바구니를 조회합니다.
     * 
     * @param cartId 장바구니 ID
     * @return 장바구니 (없으면 Optional.empty())
     */
    Optional<Cart> findById(CartId cartId);
    
    /**
     * 특정 고객의 활성 장바구니를 조회합니다.
     * 
     * @param customerId 고객 ID
     * @return 활성 장바구니 (없으면 Optional.empty())
     */
    Optional<Cart> findActiveCartByCustomerId(CustomerId customerId);
    
    /**
     * 만료된 장바구니들을 조회합니다.
     * 
     * @param expiryDate 만료 기준 시간
     * @return 만료된 장바구니 목록
     */
    List<Cart> findExpiredCarts(LocalDateTime expiryDate);
    
    /**
     * 장바구니를 삭제합니다.
     * 
     * @param cartId 삭제할 장바구니 ID
     */
    void deleteById(CartId cartId);
    
    /**
     * 장바구니 존재 여부를 확인합니다.
     * 
     * @param cartId 장바구니 ID
     * @return 존재하면 true
     */
    boolean existsById(CartId cartId);
}