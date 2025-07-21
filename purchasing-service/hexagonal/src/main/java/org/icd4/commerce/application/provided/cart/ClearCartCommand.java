package org.icd4.commerce.application.provided.cart;

import org.icd4.commerce.domain.cart.CartId;

/**
 * 장바구니 비우기 커맨드.
 * 
 * @param cartId 비울 장바구니 식별자
 */
public record ClearCartCommand(
    CartId cartId
) {
    public ClearCartCommand {
        if (cartId == null) {
            throw new IllegalArgumentException("CartId cannot be null");
        }
    }
}