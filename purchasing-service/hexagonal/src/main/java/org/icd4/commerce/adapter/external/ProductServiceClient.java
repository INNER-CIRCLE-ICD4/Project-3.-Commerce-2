package org.icd4.commerce.adapter.external;

import org.icd4.commerce.domain.common.ProductId;

import java.math.BigDecimal;

/**
 * 외부 상품 서비스와의 통신을 위한 클라이언트 인터페이스.
 * 
 * <p>구현체는 다양한 통신 방식을 사용할 수 있습니다:
 * - REST API (HTTP)
 * - gRPC
 * - 메시지 큐
 * - 직접 데이터베이스 조회
 * </p>
 */
public interface ProductServiceClient {
    
    /**
     * 상품 정보를 조회합니다.
     * 
     * @param productId 상품 ID
     * @return 상품 정보
     * @throws ProductNotFoundException 상품을 찾을 수 없는 경우
     * @throws ProductServiceException 서비스 통신 오류
     */
    ProductInfo getProduct(ProductId productId);
    
    /**
     * 상품의 재고를 확인합니다.
     * 
     * @param productId 상품 ID
     * @return 재고 수량
     * @throws ProductNotFoundException 상품을 찾을 수 없는 경우
     * @throws ProductServiceException 서비스 통신 오류
     */
    int getAvailableStock(ProductId productId);
    
    /**
     * 상품 정보 DTO.
     */
    record ProductInfo(
        String id,
        String name,
        BigDecimal price,
        boolean isActive
    ) {}
}