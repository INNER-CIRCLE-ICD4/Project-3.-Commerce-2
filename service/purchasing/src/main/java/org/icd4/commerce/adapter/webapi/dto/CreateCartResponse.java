package org.icd4.commerce.adapter.webapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.icd4.commerce.domain.cart.CartId;

/**
 * 장바구니 생성 응답 DTO.
 */
@Schema(description = "장바구니 생성 응답")
public record CreateCartResponse(
    @Schema(description = "생성된 장바구니 ID", example = "cart-abc123")
    String cartId
) {
    /**
     * CartId로부터 응답 생성.
     */
    public static CreateCartResponse from(CartId cartId) {
        return new CreateCartResponse(cartId.value());
    }
}