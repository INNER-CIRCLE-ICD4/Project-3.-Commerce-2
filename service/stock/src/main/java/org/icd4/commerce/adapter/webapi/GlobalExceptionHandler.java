package org.icd4.commerce.adapter.webapi;

import lombok.extern.slf4j.Slf4j;
import org.icd4.commerce.adapter.webapi.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.NoSuchElementException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Bean Validation 예외 처리 (@Valid 검증 실패)
     * - @NotNull, @NotBlank, @Positive 등의 검증 실패 시
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(MethodArgumentNotValidException e) {
        FieldError fieldError = e.getBindingResult().getFieldError();
        String errorMessage = fieldError != null ? fieldError.getDefaultMessage() : "입력값이 올바르지 않습니다.";
        
        log.warn("입력값 검증 실패: field={}, message={}", 
                fieldError != null ? fieldError.getField() : "unknown", errorMessage);
        
        return ResponseEntity.badRequest()
                .body(ApiResponse.error(errorMessage));
    }

    /**
     * 도메인 비즈니스 로직 예외 처리 (IllegalArgumentException)
     * - 재고 수량 검증 오류
     * - 도메인 규칙 위반 등
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgumentException(IllegalArgumentException e) {
        log.warn("비즈니스 로직 검증 실패: {}", e.getMessage());
        
        return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
    }

    /**
     * 엔티티 조회 실패 예외 처리 (NoSuchElementException)
     */
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ApiResponse<Void>> handleNoSuchElementException(NoSuchElementException e) {
        log.warn("엔티티 조회 실패: {}", e.getMessage());
        
        return ResponseEntity.status(404)
                .body(ApiResponse.error(e.getMessage()));
    }
} 