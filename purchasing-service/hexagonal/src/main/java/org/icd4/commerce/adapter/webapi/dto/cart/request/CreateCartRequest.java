package org.icd4.commerce.adapter.webapi.dto.cart.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import org.icd4.commerce.application.provided.cart.command.CreateCartCommand;
import org.icd4.commerce.domain.cart.CustomerId;

/**
 * 장바구니 생성 요청 DTO.
 */
@Builder
@Schema(description = "장바구니 생성 요청")
public record CreateCartRequest(
    @NotBlank(message = "고객 ID는 필수입니다")
    @Schema(description = "고객 ID", example = "customer-123", required = true)
    String customerId
) {
    /**
     * Command 객체로 변환.
     */
    public CreateCartCommand toCommand() {
        return new CreateCartCommand(CustomerId.of(customerId));
    }
}