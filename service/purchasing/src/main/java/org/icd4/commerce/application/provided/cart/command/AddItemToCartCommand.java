package org.icd4.commerce.application.provided.cart.command;

import org.icd4.commerce.domain.cart.CartId;
import org.icd4.commerce.domain.cart.ProductOptions;
import org.icd4.commerce.domain.common.ProductId;

/**
 * 장바구니에 상품 추가 커맨드.
 * 
 * @param cartId 장바구니 식별자
 * @param productId 상품 식별자
 * @param quantity 수량 (1-99)
 * @param options 상품 옵션
 */
public record AddItemToCartCommand(
    CartId cartId,
    ProductId productId,
    int quantity,
    ProductOptions options
) {
    public AddItemToCartCommand {
        if (cartId == null) {
            throw new IllegalArgumentException("CartId cannot be null");
        }
        if (productId == null) {
            throw new IllegalArgumentException("ProductId cannot be null");
        }
        // TODO: 해당 제품의 재고보다 많은 양을 넣을 수 없음.
        if (quantity < 1 || quantity > 99) {
            throw new IllegalArgumentException("Quantity must be between 1 and 99");
        }
        if (options == null) {
            throw new IllegalArgumentException("ProductOptions cannot be null");
        }
    }
}