package org.icd4.commerce.adapter.webapi.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * 에러 응답 형식.
 */
@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "에러 응답 정보")
public class ErrorResponse {
    
    @Schema(description = "에러 코드", example = "CART_NOT_FOUND")
    private final String code;
    
    @Schema(description = "에러 메시지", example = "장바구니를 찾을 수 없습니다.")
    private final String message;
    
    @Schema(description = "상세 에러 정보 (디버깅용)")
    private final String detail;
    
    @Schema(description = "필드별 에러 정보 (유효성 검증 실패 시)")
    private final List<FieldError> fieldErrors;
    
    /**
     * 단순 에러 응답 생성.
     */
    public static ErrorResponse of(String code, String message) {
        return ErrorResponse.builder()
            .code(code)
            .message(message)
            .build();
    }
    
    /**
     * 상세 정보 포함 에러 응답 생성.
     */
    public static ErrorResponse of(String code, String message, String detail) {
        return ErrorResponse.builder()
            .code(code)
            .message(message)
            .detail(detail)
            .build();
    }
    
    /**
     * 필드 에러 정보.
     */
    @Getter
    @Builder
    @Schema(description = "필드별 에러 정보")
    public static class FieldError {
        
        @Schema(description = "필드명", example = "quantity")
        private final String field;
        
        @Schema(description = "입력된 값", example = "0")
        private final Object value;
        
        @Schema(description = "에러 이유", example = "수량은 1 이상이어야 합니다.")
        private final String reason;
    }
}