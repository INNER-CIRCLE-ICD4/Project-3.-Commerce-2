package org.icd4.commerce.adapter.webapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Builder;
import org.icd4.commerce.application.provided.cart.UpdateCartItemQuantityCommand;
import org.icd4.commerce.domain.cart.CartId;
import org.icd4.commerce.domain.cart.CartItemId;

/**
 * 장바구니 상품 수량 변경 요청 DTO.
 */
@Builder
@Schema(description = "장바구니 상품 수량 변경 요청")
public record UpdateQuantityRequest(
    @Min(value = 1, message = "수량은 1개 이상이어야 합니다")
    @Max(value = 99, message = "수량은 99개를 초과할 수 없습니다")
    @Schema(description = "변경할 수량", example = "3", minimum = "1", maximum = "99", required = true)
    int quantity
) {
    /**
     * Command 객체로 변환.
     */
    public UpdateCartItemQuantityCommand toCommand(String cartId, String itemId) {
        return new UpdateCartItemQuantityCommand(
            CartId.of(cartId),
            CartItemId.of(itemId),
            quantity
        );
    }
}