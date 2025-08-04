package org.icd4.commerce.adapter.webapi.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * API 에러 코드 정의.
 */
@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    
    // 일반 에러
    INVALID_INPUT("INVALID_INPUT", "잘못된 입력값입니다.", HttpStatus.BAD_REQUEST),
    INTERNAL_ERROR("INTERNAL_ERROR", "서버 내부 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    
    // 장바구니 관련 에러
    CART_NOT_FOUND("CART_NOT_FOUND", "장바구니를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    CART_ALREADY_CONVERTED("CART_ALREADY_CONVERTED", "이미 주문으로 전환된 장바구니입니다.", HttpStatus.BAD_REQUEST),
    CART_ITEM_LIMIT_EXCEEDED("CART_ITEM_LIMIT_EXCEEDED", "장바구니에 담을 수 있는 상품 종류를 초과했습니다.", HttpStatus.BAD_REQUEST),
    INVALID_CART_STATE("INVALID_CART_STATE", "잘못된 장바구니 상태입니다.", HttpStatus.BAD_REQUEST),
    
    // 상품 관련 에러
    PRODUCT_NOT_FOUND("PRODUCT_NOT_FOUND", "상품을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    INSUFFICIENT_STOCK("INSUFFICIENT_STOCK", "재고가 부족합니다.", HttpStatus.BAD_REQUEST),
    INVALID_QUANTITY("INVALID_QUANTITY", "잘못된 수량입니다.", HttpStatus.BAD_REQUEST),
    
    // 외부 서비스 에러
    PRODUCT_SERVICE_ERROR("PRODUCT_SERVICE_ERROR", "상품 서비스 연동 중 오류가 발생했습니다.", HttpStatus.SERVICE_UNAVAILABLE);
    
    private final String code;
    private final String message;
    private final HttpStatus httpStatus;
}