package org.icd4.commerce.application.provided.cart;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.icd4.commerce.application.required.CartRepositoryPort;
import org.icd4.commerce.domain.cart.Cart;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 장바구니에서 상품 제거 유스케이스.
 * 
 * <p>장바구니에서 특정 아이템을 완전히 제거합니다.</p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RemoveItemFromCartUseCase {
    
    private final CartRepositoryPort cartRepository;
    
    /**
     * 장바구니에서 상품을 제거합니다.
     * 
     * @param command 상품 제거 커맨드
     * @throws CartNotFoundException 장바구니를 찾을 수 없는 경우
     * @throws org.icd4.commerce.domain.cart.CartAlreadyConvertedException 이미 주문으로 전환된 경우
     * @throws org.icd4.commerce.domain.cart.InvalidCartStateException 아이템이 존재하지 않는 경우
     */
    @Transactional
    public void execute(RemoveItemFromCartCommand command) {
        log.debug("Removing item from cart. CartId: {}, CartItemId: {}", 
            command.cartId(), command.cartItemId());
        
        Cart cart = cartRepository.findById(command.cartId())
            .orElseThrow(() -> new CartNotFoundException(command.cartId()));
        
        // 도메인 로직 실행
        cart.removeItem(command.cartItemId());
        
        cartRepository.save(cart);
        
        log.info("Item removed from cart successfully. CartId: {}, CartItemId: {}, " +
                "RemainingItems: {}", 
            cart.getId(), command.cartItemId(), cart.getItemCount());
    }
}