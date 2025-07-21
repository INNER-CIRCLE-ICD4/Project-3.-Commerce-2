package org.icd4.commerce.application.required;

import org.icd4.commerce.domain.cart.Cart;
import org.icd4.commerce.domain.cart.CartId;
import org.icd4.commerce.domain.cart.CustomerId;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 장바구니 레포지토리 포트 인터페이스.
 * 
 * <p>헥사고날 아키텍처의 아웃바운드 포트로서, 도메인 레이어에서 필요로 하는
 * 장바구니 영속성 관련 기능을 정의합니다. 실제 구현은 인프라스트럭처 레이어의
 * 어댑터에서 담당합니다.</p>
 * 
 * @author Jooeun
 * @since 1.0
 */
public interface CartRepository {
    
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
     * @return 장바구니 (Optional)
     */
    Optional<Cart> findById(CartId cartId);
    
    /**
     * 고객의 활성 장바구니를 조회합니다.
     * 전환되지 않은 장바구니만 반환합니다.
     * 
     * @param customerId 고객 ID
     * @return 활성 장바구니 (Optional)
     */
    Optional<Cart> findActiveByCustomerId(CustomerId customerId);
    
    /**
     * 고객의 모든 장바구니를 조회합니다.
     * 
     * @param customerId 고객 ID
     * @return 장바구니 목록
     */
    List<Cart> findByCustomerId(CustomerId customerId);
    
    /**
     * 특정 날짜 이전에 마지막으로 수정된 장바구니들을 조회합니다.
     * 주로 만료된 장바구니를 찾는 데 사용됩니다.
     * 
     * @param date 기준 날짜
     * @return 만료된 장바구니 목록
     */
    List<Cart> findExpiredCarts(LocalDateTime date);
    
    /**
     * 장바구니를 삭제합니다.
     * 
     * @param cartId 삭제할 장바구니 ID
     */
    void delete(CartId cartId);
    
    /**
     * 장바구니 존재 여부를 확인합니다.
     * 
     * @param cartId 장바구니 ID
     * @return 존재 여부
     */
    boolean exists(CartId cartId);
    
    /**
     * 활성 장바구니의 수를 조회합니다.
     * 
     * @return 활성 장바구니 수
     */
    long countActiveCarts();
}