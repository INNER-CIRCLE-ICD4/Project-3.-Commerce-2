package org.icd4.commerce.adapter.external;

import org.icd4.commerce.domain.cart.ProductId;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 상품 정보 집계 서비스 인터페이스.
 * 
 * <p>상품, 재고, 가격 정보를 통합하여 제공합니다.
 * 여러 외부 서비스의 정보를 조합하여 완전한 상품 상세 정보를 구성합니다.</p>
 * 
 * @author Senior Developer
 * @since 1.0
 */
public interface ProductAggregateService {
    
    /**
     * 상품의 모든 상세 정보를 조회합니다.
     * 
     * <p>상품 기본 정보, 재고 정보, 가격 정보를 병렬로 조회하여
     * 하나의 통합된 객체로 반환합니다.</p>
     * 
     * @param productId 상품 ID
     * @return 상품 상세 정보
     * @throws ProductNotFoundException 상품을 찾을 수 없는 경우
     * @throws ProductServiceException 서비스 통신 오류
     */
    ProductDetails getProductWithDetails(ProductId productId);
    
    /**
     * 여러 상품의 상세 정보를 배치로 조회합니다.
     * 
     * <p>성능 최적화를 위해 병렬 처리를 사용합니다.
     * 일부 상품 조회가 실패하더라도 성공한 상품들은 반환됩니다.</p>
     * 
     * @param productIds 상품 ID 목록
     * @return 상품 ID별 상세 정보 맵
     * @throws ProductServiceException 서비스 통신 오류
     */
    Map<ProductId, ProductDetails> getProductsWithDetails(List<ProductId> productIds);
    
    /**
     * 상품 상세 정보 DTO.
     * 
     * <p>상품의 모든 정보를 통합한 불변 객체입니다.</p>
     */
    record ProductDetails(
        ProductInfo product,
        StockInfo stock,
        PriceInfo price,
        LocalDateTime retrievedAt  // 조회 시점
    ) {
        /**
         * 구매 가능 여부를 확인합니다.
         */
        public boolean isPurchasable() {
            return product.isActive() && 
                   stock.hasStock() && 
                   price.isValid();
        }
        
        /**
         * 요청한 수량만큼 구매 가능한지 확인합니다.
         */
        public boolean canPurchase(int quantity) {
            return isPurchasable() && stock.hasStock(quantity);
        }
    }
    
    /**
     * 상품 기본 정보.
     */
    record ProductInfo(
        String id,
        String name,
        String brand,
        String description,
        String categoryId,
        boolean isActive
    ) {}
    
    /**
     * 재고 정보.
     */
    record StockInfo(
        int availableQuantity,
        int reservedQuantity,
        StockStatus status,
        LocalDateTime lastUpdated
    ) {
        public enum StockStatus {
            AVAILABLE, LOW_STOCK, OUT_OF_STOCK
        }
        
        public boolean hasStock() {
            return availableQuantity > 0;
        }
        
        public boolean hasStock(int quantity) {
            return availableQuantity >= quantity;
        }
    }
    
    /**
     * 가격 정보.
     */
    record PriceInfo(
        BigDecimal basePrice,
        BigDecimal finalPrice,
        String currency,
        List<Discount> discounts,
        LocalDateTime validUntil
    ) {
        public boolean isValid() {
            return validUntil == null || validUntil.isAfter(LocalDateTime.now());
        }
        
        public record Discount(
            String type,
            BigDecimal amount,
            String description
        ) {}
    }
}