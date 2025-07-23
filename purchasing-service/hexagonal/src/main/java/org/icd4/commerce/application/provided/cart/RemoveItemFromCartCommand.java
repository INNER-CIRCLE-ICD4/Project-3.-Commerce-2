package org.icd4.commerce.application.provided.cart;

import org.icd4.commerce.domain.cart.CartId;
import org.icd4.commerce.domain.cart.CartItemId;

/**
 * 장바구니에서 상품 제거 커맨드.
 * 
 * @param cartId 장바구니 식별자
 * @param cartItemId 제거할 장바구니 아이템 식별자
 */
public record RemoveItemFromCartCommand(
    CartId cartId,
    CartItemId cartItemId
) {
    public RemoveItemFromCartCommand {
        if (cartId == null) {
            throw new IllegalArgumentException("CartId cannot be null");
        }
        if (cartItemId == null) {
            throw new IllegalArgumentException("CartItemId cannot be null");
        }
    }
}