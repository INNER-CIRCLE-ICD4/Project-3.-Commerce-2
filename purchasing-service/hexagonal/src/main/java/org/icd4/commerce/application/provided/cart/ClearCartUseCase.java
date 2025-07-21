package org.icd4.commerce.application.provided.cart;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.icd4.commerce.application.required.CartRepositoryPort;
import org.icd4.commerce.domain.cart.Cart;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 장바구니 비우기 유스케이스.
 * 
 * <p>장바구니의 모든 상품을 제거합니다.
 * 장바구니 자체는 삭제되지 않고 빈 상태로 유지됩니다.</p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ClearCartUseCase {
    
    private final CartRepositoryPort cartRepository;
    
    /**
     * 장바구니를 비웁니다.
     * 
     * @param command 장바구니 비우기 커맨드
     * @throws CartNotFoundException 장바구니를 찾을 수 없는 경우
     * @throws org.icd4.commerce.domain.cart.CartAlreadyConvertedException 이미 주문으로 전환된 경우
     */
    @Transactional
    public void execute(ClearCartCommand command) {
        log.debug("Clearing cart: {}", command.cartId());
        
        Cart cart = cartRepository.findById(command.cartId())
            .orElseThrow(() -> new CartNotFoundException(command.cartId()));
        
        int previousItemCount = cart.getItemCount();
        
        // 도메인 로직 실행
        cart.clear();
        
        cartRepository.save(cart);
        
        log.info("Cart cleared successfully. CartId: {}, RemovedItems: {}", 
            cart.getId(), previousItemCount);
    }
}