package org.icd4.commerce.adapter.persistence;

import org.icd4.commerce.application.required.CartRepositoryPort;
import org.icd4.commerce.domain.cart.Cart;
import org.icd4.commerce.domain.cart.CartId;
import org.icd4.commerce.domain.cart.CustomerId;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * CartRepositoryPort의 JPA 구현체.
 * 
 * <p>헥사고날 아키텍처의 어댑터로서, 도메인 포트를 JPA Repository로 구현합니다.
 * 이를 통해 도메인 레이어가 JPA에 직접 의존하지 않도록 합니다.</p>
 * 
 * @author Jooeun
 * @since 1.0
 */
@Component
@Transactional(readOnly = true)
public class CartRepositoryAdapter implements CartRepositoryPort {
    
    private final CartRepository cartRepository;
    
    public CartRepositoryAdapter(CartRepository cartRepository) {
        this.cartRepository = cartRepository;
    }
    
    @Override
    @Transactional
    public Cart save(Cart cart) {
        return cartRepository.save(cart);
    }
    
    @Override
    public Optional<Cart> findById(CartId cartId) {
        return cartRepository.findById(cartId);
    }
    
    @Override
    public Optional<Cart> findActiveCartByCustomerId(CustomerId customerId) {
        return cartRepository.findActiveCartByCustomerId(customerId);
    }
    
    @Override
    public List<Cart> findExpiredCarts(LocalDateTime expiryDate) {
        return cartRepository.findExpiredCarts(expiryDate);
    }
    
    @Override
    @Transactional
    public void deleteById(CartId cartId) {
        cartRepository.deleteById(cartId);
    }
    
    @Override
    public boolean existsById(CartId cartId) {
        return cartRepository.existsById(cartId);
    }
}