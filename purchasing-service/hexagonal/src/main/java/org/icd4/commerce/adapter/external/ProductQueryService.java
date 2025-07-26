package org.icd4.commerce.adapter.external;

import org.icd4.commerce.domain.cart.ProductId;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 상품 조회 서비스 인터페이스.
 * 
 * <p>상품 정보를 조회하는 다양한 방법을 제공합니다.
 * 단일 조회, 배치 조회, Optional 기반 조회를 지원합니다.</p>
 * 
 * @author Senior Developer
 * @since 1.0
 */
public interface ProductQueryService {
    
    /**
     * 단일 상품을 조회합니다.
     * 
     * @param productId 상품 ID
     * @return 상품 정보
     * @throws ProductNotFoundException 상품을 찾을 수 없는 경우
     * @throws ProductServiceException 서비스 통신 오류
     */
    ProductInfo getProduct(ProductId productId);
    
    /**
     * 여러 상품을 배치로 조회합니다.
     * 
     * <p>N+1 문제를 방지하기 위해 한 번의 요청으로 여러 상품을 조회합니다.
     * 일부 상품 조회가 실패하더라도 성공한 상품들은 반환됩니다.</p>
     * 
     * @param productIds 상품 ID 목록
     * @return 상품 ID별 상품 정보 맵 (조회 실패한 상품은 제외)
     * @throws ProductServiceException 서비스 통신 오류
     */
    Map<ProductId, ProductInfo> getProducts(List<ProductId> productIds);
    
    /**
     * 상품을 Optional로 조회합니다.
     * 
     * <p>상품이 없을 때 예외 대신 Optional.empty()를 반환합니다.
     * 존재 여부가 불확실한 경우에 사용합니다.</p>
     * 
     * @param productId 상품 ID
     * @return 상품 정보 Optional
     * @throws ProductServiceException 서비스 통신 오류 (404 제외)
     */
    Optional<ProductInfo> findProduct(ProductId productId);
    
    /**
     * 상품 정보 DTO.
     * 
     * <p>외부 상품 서비스로부터 조회한 상품 정보를 담는 불변 객체입니다.</p>
     */
    record ProductInfo(
        String id,
        String name,
        String brand,
        String description,
        java.math.BigDecimal price,
        String currency,
        int availableStock,
        boolean isActive,
        ProductStatus status
    ) {
        /**
         * 상품 상태.
         */
        public enum ProductStatus {
            ON_SALE,        // 판매중
            OUT_OF_STOCK,   // 품절
            STOPPED         // 판매중지
        }
    }
}