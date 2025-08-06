package org.icd4.commerce.adapter.webapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import org.icd4.commerce.application.provided.cart.command.MergeCartsCommand;
import org.icd4.commerce.domain.cart.CartId;

/**
 * 장바구니 병합 요청 DTO.
 */
@Builder
@Schema(description = "장바구니 병합 요청")
public record MergeCartsRequest(
    @NotBlank(message = "소스 장바구니 ID는 필수입니다")
    @Schema(description = "병합할 장바구니 ID (소스)", example = "cart-temp123", required = true)
    String sourceCartId,
    
    @Schema(description = "병합 후 소스 장바구니 삭제 여부", example = "true", defaultValue = "false")
    boolean deleteSourceCart
) {
    /**
     * Command 객체로 변환.
     */
    public MergeCartsCommand toCommand(String targetCartId) {
        return new MergeCartsCommand(
            CartId.of(targetCartId),
            CartId.of(sourceCartId),
            deleteSourceCart
        );
    }
}