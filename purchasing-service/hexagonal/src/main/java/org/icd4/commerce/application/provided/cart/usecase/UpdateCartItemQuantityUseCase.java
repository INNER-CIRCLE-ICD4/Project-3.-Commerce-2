package org.icd4.commerce.application.provided.cart.usecase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.icd4.commerce.application.provided.cart.CartNotFoundException;
import org.icd4.commerce.application.provided.cart.command.UpdateCartItemQuantityCommand;
import org.icd4.commerce.application.required.CartRepositoryPort;
import org.icd4.commerce.domain.cart.Cart;
import org.icd4.commerce.domain.cart.exception.CartAlreadyConvertedException;
import org.icd4.commerce.domain.cart.exception.InvalidCartStateException;
import org.icd4.commerce.domain.cart.exception.InvalidQuantityException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 장바구니 상품 수량 변경 유스케이스.
 * 
 * <p>장바구니에 있는 특정 상품의 수량을 변경합니다.
 * 수량은 1에서 99 사이의 값이어야 합니다.</p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UpdateCartItemQuantityUseCase {
    
    private final CartRepositoryPort cartRepository;
    
    /**
     * 장바구니 상품의 수량을 변경합니다.
     * 
     * @param command 수량 변경 커맨드
     * @throws CartNotFoundException 장바구니를 찾을 수 없는 경우
     * @throws CartAlreadyConvertedException 이미 주문으로 전환된 경우
     * @throws InvalidCartStateException 아이템이 존재하지 않는 경우
     * @throws InvalidQuantityException 수량이 유효하지 않은 경우
     */
    @Transactional
    public void execute(UpdateCartItemQuantityCommand command) {
        log.debug("Updating cart item quantity. CartId: {}, CartItemId: {}, NewQuantity: {}", 
            command.cartId(), command.cartItemId(), command.quantity());
        
        Cart cart = cartRepository.findById(command.cartId())
            .orElseThrow(() -> new CartNotFoundException(command.cartId()));
        
        // 도메인 로직 실행
        cart.updateQuantity(command.cartItemId(), command.quantity());
        
        cartRepository.save(cart);
        
        log.info("Cart item quantity updated successfully. CartId: {}, CartItemId: {}, " +
                "NewQuantity: {}", 
            cart.getId(), command.cartItemId(), command.quantity());
    }
}