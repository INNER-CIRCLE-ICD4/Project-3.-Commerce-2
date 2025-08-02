package org.icd4.commerce.adapter.webapi.dto.cart.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import org.icd4.commerce.application.provided.cart.CartResult;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 장바구니 아이템 응답 DTO.
 */
@Builder
@Schema(description = "장바구니 아이템 정보")
public record CartItemResponse(
    @Schema(description = "아이템 ID", example = "item-xyz789")
    String id,
    
    @Schema(description = "상품 ID", example = "1")
    String productId,
    
    @Schema(description = "수량", example = "2")
    int quantity,
    
    @Schema(description = "상품 옵션", example = "{\"size\": \"L\", \"color\": \"blue\"}")
    Map<String, String> options,
    
    @Schema(description = "추가 일시", example = "2024-01-20T10:45:00")
    LocalDateTime addedAt
) {
    /**
     * CartItemResult로부터 응답 생성.
     */
    public static CartItemResponse from(CartResult.CartItemResult result) {
        return CartItemResponse.builder()
            .id(result.id())
            .productId(result.productId())
            .quantity(result.quantity())
            .options(result.options().options())
            .addedAt(result.addedAt())
            .build();
    }
}