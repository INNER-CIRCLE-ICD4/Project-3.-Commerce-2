package org.icd4.commerce.adapter.webapi.dto.cart.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import org.icd4.commerce.application.provided.cart.command.AddItemToCartCommand;
import org.icd4.commerce.domain.cart.CartId;
import org.icd4.commerce.domain.cart.ProductOptions;
import org.icd4.commerce.domain.common.ProductId;
import org.icd4.commerce.domain.common.StockKeepingUnit;

import java.util.Map;

@Builder
@Schema(description = "장바구니 상품 추가 요청")
public record AddItemRequest(
        @NotBlank(message = "상품 ID는 필수입니다")
        @Schema(description = "상품 ID", example = "UUID", required = true)
        String productId,

        @NotBlank(message = "상품 unit id는 필수입니다")
        @Schema(description = "sku", example = "UUID_HEX", required = true)
        String sku,

        @Min(value = 1, message = "수량은 1개 이상이어야 합니다")
        @Max(value = 99, message = "수량은 99개를 초과할 수 없습니다")
        @Schema(description = "수량", example = "2", minimum = "1", maximum = "99", required = true)
        int quantity,

        @Schema(description = "상품 옵션 (선택사항)", example = "{\"size\": \"L\", \"color\": \"blue\"}")
        Map<String, String> options
) {
    public AddItemToCartCommand toCommand(String cartId) {
        return new AddItemToCartCommand(
                CartId.of(cartId),
                ProductId.of(productId),
                StockKeepingUnit.of(sku),
                quantity,
                options != null ? ProductOptions.of(options) : ProductOptions.empty()
        );
    }
}