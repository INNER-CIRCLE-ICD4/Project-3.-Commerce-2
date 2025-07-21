package org.icd4.commerce.application.provided.cart;

import org.icd4.commerce.domain.cart.CartId;

/**
 * 장바구니를 찾을 수 없을 때 발생하는 예외.
 */
public class CartNotFoundException extends RuntimeException {
    
    private final CartId cartId;
    
    public CartNotFoundException(CartId cartId) {
        super(String.format("Cart not found: %s", cartId.value()));
        this.cartId = cartId;
    }
    
    public CartId getCartId() {
        return cartId;
    }
}