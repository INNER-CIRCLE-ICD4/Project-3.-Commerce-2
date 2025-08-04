package org.icd4.commerce.adapter.webapi.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 표준화된 API 응답 래퍼.
 * 
 * @param <T> 응답 데이터 타입
 */
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "표준 API 응답 형식")
public class ApiResponse<T> {
    
    @Schema(description = "응답 성공 여부", example = "true")
    private final boolean success;
    
    @Schema(description = "응답 데이터")
    private final T data;
    
    @Schema(description = "에러 정보 (실패 시에만 포함)")
    private final ErrorResponse error;
    
    @Schema(description = "응답 시간", example = "2024-01-20T10:30:00")
    private final LocalDateTime timestamp;
    
    private ApiResponse(boolean success, T data, ErrorResponse error) {
        this.success = success;
        this.data = data;
        this.error = error;
        this.timestamp = LocalDateTime.now();
    }
    
    /**
     * 성공 응답 생성.
     */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, data, null);
    }
    
    /**
     * 데이터 없는 성공 응답 생성.
     */
    public static ApiResponse<Void> success() {
        return new ApiResponse<>(true, null, null);
    }
    
    /**
     * 실패 응답 생성.
     */
    public static <T> ApiResponse<T> error(ErrorResponse error) {
        return new ApiResponse<>(false, null, error);
    }
    
    /**
     * 실패 응답 생성 (간편 메서드).
     */
    public static <T> ApiResponse<T> error(String code, String message) {
        return new ApiResponse<>(false, null, ErrorResponse.of(code, message));
    }
}