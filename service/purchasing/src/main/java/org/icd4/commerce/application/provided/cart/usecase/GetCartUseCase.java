package org.icd4.commerce.application.provided.cart.usecase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.icd4.commerce.application.provided.cart.exception.CartNotFoundException;
import org.icd4.commerce.application.provided.cart.CartResult;
import org.icd4.commerce.application.required.CartRepositoryPort;
import org.icd4.commerce.domain.cart.Cart;
import org.icd4.commerce.domain.cart.CartId;
import org.icd4.commerce.domain.cart.ProductPriceProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * 장바구니 조회 유스케이스.
 * 
 * <p>장바구니 정보와 함께 현재 상품 가격을 기준으로
 * 총 금액을 계산하여 제공합니다.</p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GetCartUseCase {
    
    private final CartRepositoryPort cartRepository;
    private final ProductPriceProvider productPriceProvider;
    
    /**
     * 장바구니를 조회합니다.
     * 
     * @param cartId 조회할 장바구니 ID
     * @return 장바구니 정보와 총 금액
     * @throws CartNotFoundException 장바구니를 찾을 수 없는 경우
     */
    @Transactional(readOnly = true)
    public CartResult execute(CartId cartId) {
        log.debug("Getting cart: {}", cartId);
        
        Cart cart = cartRepository.findById(cartId)
            .orElseThrow(() -> new CartNotFoundException(cartId));
        
        // 현재 가격 기준으로 총 금액 계산
        BigDecimal totalAmount = cart.calculateTotal(productPriceProvider);
        
        log.debug("Cart retrieved. CartId: {}, ItemCount: {}, TotalAmount: {}", 
            cart.getId(), cart.getItemCount(), totalAmount);
        
        return CartResult.from(cart, totalAmount);
    }
}