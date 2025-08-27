package org.icd4.commerce.adapter.webapi;

import lombok.extern.slf4j.Slf4j;
import org.icd4.commerce.adapter.webapi.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.NoSuchElementException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * JSON 파싱 예외 처리 (HttpMessageNotReadableException)
     * - 잘못된 JSON 형식, 타입 불일치 등
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        log.warn("JSON 파싱 실패: {}", e.getMessage());
        
        return ResponseEntity.badRequest()
                .body(ApiResponse.error("잘못된 요청 형식입니다. JSON 형식을 확인해주세요."));
    }

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

    /**
     * 모든 예외 처리 (기타 예상치 못한 예외들)
     * - 위에서 처리되지 않은 모든 예외를 캐치
     * - 시스템 오류, 데이터베이스 연결 오류 등
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception e) {
        log.error("예상치 못한 오류 발생: {}", e.getMessage(), e);
        
        return ResponseEntity.status(500)
                .body(ApiResponse.error("서버 내부 오류가 발생했습니다. 잠시 후 다시 시도해주세요."));
    }
} 