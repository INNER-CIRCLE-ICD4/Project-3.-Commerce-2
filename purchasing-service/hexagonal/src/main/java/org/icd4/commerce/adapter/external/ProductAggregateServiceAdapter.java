package org.icd4.commerce.adapter.external;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.icd4.commerce.domain.cart.ProductId;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * ProductAggregateService 구현체.
 * 
 * <p>상품, 재고, 가격 정보를 병렬로 조회하여 통합된 상품 상세 정보를 제공합니다.
 * 성능 최적화를 위해 비동기 처리와 병렬 실행을 활용합니다.</p>
 * 
 * @author Senior Developer
 * @since 1.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ProductAggregateServiceAdapter implements ProductAggregateService {
    
    private final ProductQueryService productQueryService;
    private final ProductServiceClient productServiceClient;
    private final ProductPriceProviderAdapter priceProviderAdapter;
    
    // 병렬 처리를 위한 스레드풀
    private final ExecutorService executorService = Executors.newFixedThreadPool(10);
    
    @Override
    public ProductDetails getProductWithDetails(ProductId productId) {
        log.debug("Getting product details for: {}", productId);
        
        LocalDateTime startTime = LocalDateTime.now();
        
        try {
            // 병렬로 정보 조회
            CompletableFuture<ProductInfo> productFuture = 
                CompletableFuture.supplyAsync(() -> fetchProductInfo(productId), executorService);
                
            CompletableFuture<StockInfo> stockFuture = 
                CompletableFuture.supplyAsync(() -> fetchStockInfo(productId), executorService);
                
            CompletableFuture<PriceInfo> priceFuture = 
                CompletableFuture.supplyAsync(() -> fetchPriceInfo(productId), executorService);
            
            // 모든 정보가 조회될 때까지 대기
            CompletableFuture.allOf(productFuture, stockFuture, priceFuture).join();
            
            // 결과 조합
            ProductDetails details = new ProductDetails(
                productFuture.join(),
                stockFuture.join(),
                priceFuture.join(),
                LocalDateTime.now()
            );
            
            log.debug("Product details retrieved successfully for {} in {}ms", 
                productId, 
                java.time.Duration.between(startTime, LocalDateTime.now()).toMillis());
            
            return details;
            
        } catch (CompletionException e) {
            Throwable cause = e.getCause();
            if (cause instanceof ProductNotFoundException) {
                throw (ProductNotFoundException) cause;
            }
            throw new ProductServiceException("Failed to get product details", cause);
        } catch (Exception e) {
            log.error("Error getting product details for: {}", productId, e);
            throw new ProductServiceException("Failed to get product details", e);
        }
    }
    
    @Override
    public Map<ProductId, ProductDetails> getProductsWithDetails(List<ProductId> productIds) {
        if (productIds == null || productIds.isEmpty()) {
            return Collections.emptyMap();
        }
        
        log.debug("Getting details for {} products", productIds.size());
        
        // 중복 제거
        List<ProductId> uniqueIds = productIds.stream()
            .distinct()
            .toList();
        
        // 각 상품에 대해 병렬로 상세 정보 조회
        Map<ProductId, CompletableFuture<ProductDetails>> futures = uniqueIds.stream()
            .collect(Collectors.toMap(
                id -> id,
                id -> CompletableFuture.supplyAsync(
                    () -> getProductWithDetailsQuietly(id), 
                    executorService
                )
            ));
        
        // 모든 조회가 완료될 때까지 대기
        CompletableFuture.allOf(futures.values().toArray(new CompletableFuture[0])).join();
        
        // 성공한 결과만 수집
        Map<ProductId, ProductDetails> result = new HashMap<>();
        futures.forEach((id, future) -> {
            try {
                ProductDetails details = future.join();
                if (details != null) {
                    result.put(id, details);
                }
            } catch (Exception e) {
                log.warn("Failed to get details for product: {}", id, e);
            }
        });
        
        log.debug("Retrieved details for {}/{} products", result.size(), uniqueIds.size());
        return result;
    }
    
    /**
     * 상품 기본 정보를 조회합니다.
     */
    private ProductInfo fetchProductInfo(ProductId productId) {
        ProductQueryService.ProductInfo queryInfo = productQueryService.getProduct(productId);
        
        return new ProductInfo(
            queryInfo.id(),
            queryInfo.name(),
            queryInfo.brand(),
            queryInfo.description(),
            null,  // categoryId는 현재 제공되지 않음
            queryInfo.isActive()
        );
    }
    
    /**
     * 재고 정보를 조회합니다.
     */
    private StockInfo fetchStockInfo(ProductId productId) {
        int availableStock = productServiceClient.getAvailableStock(productId);
        
        StockInfo.StockStatus status;
        if (availableStock <= 0) {
            status = StockInfo.StockStatus.OUT_OF_STOCK;
        } else if (availableStock <= 10) {
            status = StockInfo.StockStatus.LOW_STOCK;
        } else {
            status = StockInfo.StockStatus.AVAILABLE;
        }
        
        return new StockInfo(
            availableStock,
            0,  // reservedQuantity는 현재 제공되지 않음
            status,
            LocalDateTime.now()
        );
    }
    
    /**
     * 가격 정보를 조회합니다.
     */
    private PriceInfo fetchPriceInfo(ProductId productId) {
        BigDecimal price = priceProviderAdapter.getPrice(productId);
        
        return new PriceInfo(
            price,
            price,  // 현재는 할인이 없으므로 basePrice = finalPrice
            "KRW",
            List.of(),  // 할인 정보 없음
            null  // 가격 유효 기간 없음
        );
    }
    
    /**
     * 예외를 던지지 않고 상품 상세 정보를 조회합니다.
     * 배치 처리 시 부분 실패를 허용하기 위해 사용됩니다.
     */
    private ProductDetails getProductWithDetailsQuietly(ProductId productId) {
        try {
            return getProductWithDetails(productId);
        } catch (ProductNotFoundException e) {
            log.debug("Product not found during batch processing: {}", productId);
            return null;
        } catch (Exception e) {
            log.warn("Error getting product details during batch processing: {}", productId, e);
            return null;
        }
    }
}