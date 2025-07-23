package org.icd4.commerce.adapter.external;

import org.icd4.commerce.domain.cart.ProductId;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * 비동기 상품 조회 서비스 인터페이스.
 * 
 * <p>논블로킹 I/O를 통해 성능을 향상시키고,
 * 여러 상품을 동시에 조회할 수 있습니다.</p>
 * 
 * @author Senior Developer
 * @since 1.0
 */
public interface AsyncProductService {
    
    /**
     * 상품을 비동기로 조회합니다.
     * 
     * @param productId 상품 ID
     * @return 상품 정보 CompletableFuture
     */
    CompletableFuture<ProductQueryService.ProductInfo> getProductAsync(ProductId productId);
    
    /**
     * 여러 상품을 비동기로 조회합니다.
     * 
     * <p>모든 상품 조회가 병렬로 실행되며, 
     * 개별 실패는 전체 결과에 영향을 주지 않습니다.</p>
     * 
     * @param productIds 상품 ID 목록
     * @return 상품 정보 맵 CompletableFuture
     */
    CompletableFuture<Map<ProductId, ProductQueryService.ProductInfo>> getProductsAsync(
        List<ProductId> productIds
    );
    
    /**
     * 상품 상세 정보를 비동기로 조회합니다.
     * 
     * <p>상품, 재고, 가격 정보를 병렬로 조회합니다.</p>
     * 
     * @param productId 상품 ID
     * @return 상품 상세 정보 CompletableFuture
     */
    CompletableFuture<ProductAggregateService.ProductDetails> getProductDetailsAsync(
        ProductId productId
    );
    
    /**
     * 재고 정보를 비동기로 조회합니다.
     * 
     * @param productId 상품 ID
     * @return 재고 수량 CompletableFuture
     */
    CompletableFuture<Integer> getStockAsync(ProductId productId);
    
    /**
     * 가격 정보를 비동기로 조회합니다.
     * 
     * @param productId 상품 ID
     * @return 가격 정보 CompletableFuture
     */
    CompletableFuture<ProductAggregateService.PriceInfo> getPriceAsync(ProductId productId);
}