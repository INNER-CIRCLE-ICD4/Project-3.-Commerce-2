package org.icd4.commerce.adapter.webapi.exception;

import lombok.extern.slf4j.Slf4j;
import org.icd4.commerce.adapter.external.ProductNotFoundException;
import org.icd4.commerce.adapter.external.ProductServiceException;
import org.icd4.commerce.adapter.webapi.common.ApiResponse;
import org.icd4.commerce.adapter.webapi.common.ErrorCode;
import org.icd4.commerce.adapter.webapi.common.ErrorResponse;
import org.icd4.commerce.application.provided.cart.CartNotFoundException;
import org.icd4.commerce.application.provided.cart.InsufficientStockException;
import org.icd4.commerce.domain.cart.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 전역 예외 처리기.
 * 
 * <p>모든 컨트롤러에서 발생하는 예외를 중앙에서 처리하여
 * 일관된 에러 응답을 제공합니다.</p>
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    /**
     * 입력값 검증 실패 처리.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(
            MethodArgumentNotValidException ex) {
        
        List<ErrorResponse.FieldError> fieldErrors = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(this::createFieldError)
            .collect(Collectors.toList());
        
        ErrorResponse error = ErrorResponse.builder()
            .code(ErrorCode.INVALID_INPUT.getCode())
            .message("입력값 검증에 실패했습니다.")
            .fieldErrors(fieldErrors)
            .build();
        
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.error(error));
    }
    
    /**
     * 타입 변환 실패 처리.
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Void>> handleTypeMismatchException(
            MethodArgumentTypeMismatchException ex) {
        
        String message = String.format(
            "'%s' 파라미터의 값 '%s'을(를) %s 타입으로 변환할 수 없습니다.",
            ex.getName(), ex.getValue(), ex.getRequiredType().getSimpleName()
        );
        
        return createErrorResponse(ErrorCode.INVALID_INPUT, message);
    }
    
    /**
     * 장바구니 관련 예외 처리.
     */
    @ExceptionHandler(CartNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleCartNotFoundException(
            CartNotFoundException ex) {
        log.debug("Cart not found: {}", ex.getCartId());
        return createErrorResponse(ErrorCode.CART_NOT_FOUND);
    }
    
    @ExceptionHandler(CartAlreadyConvertedException.class)
    public ResponseEntity<ApiResponse<Void>> handleCartAlreadyConvertedException(
            CartAlreadyConvertedException ex) {
        return createErrorResponse(ErrorCode.CART_ALREADY_CONVERTED);
    }
    
    @ExceptionHandler(CartItemLimitExceededException.class)
    public ResponseEntity<ApiResponse<Void>> handleCartItemLimitExceededException(
            CartItemLimitExceededException ex) {
        return createErrorResponse(ErrorCode.CART_ITEM_LIMIT_EXCEEDED, ex.getMessage());
    }
    
    @ExceptionHandler(InvalidCartStateException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidCartStateException(
            InvalidCartStateException ex) {
        return createErrorResponse(ErrorCode.INVALID_CART_STATE, ex.getMessage());
    }
    
    @ExceptionHandler(InvalidQuantityException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidQuantityException(
            InvalidQuantityException ex) {
        return createErrorResponse(ErrorCode.INVALID_QUANTITY, ex.getMessage());
    }
    
    /**
     * 재고 부족 예외 처리.
     */
    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<ApiResponse<Void>> handleInsufficientStockException(
            InsufficientStockException ex) {
        
        String detail = String.format(
            "상품 %s: 재고 %d개, 요청 %d개",
            ex.getProductId().value(),
            ex.getAvailableStock(),
            ex.getRequestedQuantity()
        );
        
        ErrorResponse error = ErrorResponse.of(
            ErrorCode.INSUFFICIENT_STOCK.getCode(),
            ErrorCode.INSUFFICIENT_STOCK.getMessage(),
            detail
        );
        
        return ResponseEntity
            .status(ErrorCode.INSUFFICIENT_STOCK.getHttpStatus())
            .body(ApiResponse.error(error));
    }
    
    /**
     * 상품 서비스 관련 예외 처리.
     */
    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleProductNotFoundException(
            ProductNotFoundException ex) {
        log.debug("Product not found: {}", ex.getProductId());
        return createErrorResponse(ErrorCode.PRODUCT_NOT_FOUND);
    }
    
    @ExceptionHandler(ProductServiceException.class)
    public ResponseEntity<ApiResponse<Void>> handleProductServiceException(
            ProductServiceException ex) {
        log.error("Product service error", ex);
        return createErrorResponse(ErrorCode.PRODUCT_SERVICE_ERROR);
    }
    
    /**
     * 일반 예외 처리.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgumentException(
            IllegalArgumentException ex) {
        return createErrorResponse(ErrorCode.INVALID_INPUT, ex.getMessage());
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGeneralException(Exception ex) {
        log.error("Unexpected error occurred", ex);
        return createErrorResponse(ErrorCode.INTERNAL_ERROR);
    }
    
    /**
     * 에러 응답 생성 헬퍼 메서드.
     */
    private ResponseEntity<ApiResponse<Void>> createErrorResponse(ErrorCode errorCode) {
        return ResponseEntity
            .status(errorCode.getHttpStatus())
            .body(ApiResponse.error(errorCode.getCode(), errorCode.getMessage()));
    }
    
    private ResponseEntity<ApiResponse<Void>> createErrorResponse(
            ErrorCode errorCode, String message) {
        return ResponseEntity
            .status(errorCode.getHttpStatus())
            .body(ApiResponse.error(errorCode.getCode(), message));
    }
    
    private ErrorResponse.FieldError createFieldError(FieldError fieldError) {
        return ErrorResponse.FieldError.builder()
            .field(fieldError.getField())
            .value(fieldError.getRejectedValue())
            .reason(fieldError.getDefaultMessage())
            .build();
    }
}