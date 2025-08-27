package org.icd4.commerce.adapter.external.exception;

/**
 * 상품 서비스 통신 중 발생하는 예외.
 */
public class ProductServiceException extends RuntimeException {
    
    public ProductServiceException(String message) {
        super(message);
    }
    
    public ProductServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}