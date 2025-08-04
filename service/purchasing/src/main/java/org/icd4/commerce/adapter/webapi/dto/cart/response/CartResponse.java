package org.icd4.commerce.adapter.webapi.dto.cart.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import org.icd4.commerce.application.provided.cart.CartResult;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 장바구니 조회 응답 DTO.
 */
@Builder
@Schema(description = "장바구니 정보")
public record CartResponse(
    @Schema(description = "장바구니 ID", example = "cart-abc123")
    String id,
    
    @Schema(description = "고객 ID", example = "customer-123")
    String customerId,
    
    @Schema(description = "장바구니 아이템 목록")
    List<CartItemResponse> items,
    
    @Schema(description = "전체 수량", example = "5")
    int totalQuantity,
    
    @Schema(description = "총 금액", example = "50000.00")
    BigDecimal totalAmount,
    
    @Schema(description = "생성 일시", example = "2024-01-20T10:30:00")
    LocalDateTime createdAt,
    
    @Schema(description = "마지막 수정 일시", example = "2024-01-20T11:00:00")
    LocalDateTime lastModifiedAt,
    
    @Schema(description = "주문 전환 여부", example = "false")
    boolean isConverted
) {
    /**
     * CartResult로부터 응답 생성.
     */
    public static CartResponse from(CartResult result) {
        List<CartItemResponse> itemResponses = result.items().stream()
            .map(CartItemResponse::from)
            .toList();
            
        return CartResponse.builder()
            .id(result.id().value())
            .customerId(result.customerId().value())
            .items(itemResponses)
            .totalQuantity(result.totalQuantity())
            .totalAmount(result.totalAmount())
            .createdAt(result.createdAt())
            .lastModifiedAt(result.lastModifiedAt())
            .isConverted(result.isConverted())
            .build();
    }
}