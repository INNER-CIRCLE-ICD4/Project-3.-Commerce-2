package org.icd4.commerce.application.provided.cart.command;

import org.icd4.commerce.domain.cart.CartId;
import org.icd4.commerce.domain.cart.CartItemId;

/**
 * 장바구니 상품 수량 변경 커맨드.
 * 
 * @param cartId 장바구니 식별자
 * @param cartItemId 장바구니 아이템 식별자
 * @param quantity 변경할 수량 (1-99)
 */
public record UpdateCartItemQuantityCommand(
    CartId cartId,
    CartItemId cartItemId,
    int quantity
) {
    public UpdateCartItemQuantityCommand {
        if (cartId == null) {
            throw new IllegalArgumentException("CartId cannot be null");
        }
        if (cartItemId == null) {
            throw new IllegalArgumentException("CartItemId cannot be null");
        }
        if (quantity < 1 || quantity > 99) {
            throw new IllegalArgumentException("Quantity must be between 1 and 99");
        }
    }
}