package org.icd4.commerce.application.provided.cart;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.icd4.commerce.application.required.CartRepositoryPort;
import org.icd4.commerce.domain.cart.Cart;
import org.icd4.commerce.domain.cart.CartId;
import org.icd4.commerce.domain.cart.TimeProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 장바구니 생성 유스케이스.
 * 
 * <p>고객을 위한 새로운 장바구니를 생성합니다.
 * 한 고객은 여러 개의 장바구니를 가질 수 있으며,
 * 이는 장치별로 다른 장바구니를 관리하거나
 * 임시 저장 용도로 활용될 수 있습니다.</p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CreateCartUseCase {
    
    private final CartRepositoryPort cartRepository;
    private final TimeProvider timeProvider;
    
    /**
     * 새로운 장바구니를 생성합니다.
     * 
     * @param command 장바구니 생성 커맨드
     * @return 생성된 장바구니 ID
     */
    @Transactional
    public CartId execute(CreateCartCommand command) {
        log.debug("Creating new cart for customer: {}", command.customerId());
        
        CartId cartId = CartId.generate();
        Cart cart = new Cart(
            cartId,
            command.customerId(),
            timeProvider
        );
        
        Cart savedCart = cartRepository.save(cart);
        
        log.info("Cart created successfully. CartId: {}, CustomerId: {}", 
            savedCart.getId(), savedCart.getCustomerId());
        
        return savedCart.getId();
    }
}