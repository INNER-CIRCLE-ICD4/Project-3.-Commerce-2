package org.icd4.commerce.adapter.external;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.icd4.commerce.domain.cart.ProductId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * AsyncProductService 구현체.
 * 
 * <p>비동기 상품 조회 기능을 제공합니다.
 * 설정 가능한 스레드풀을 사용하여 성능을 최적화합니다.</p>
 * 
 * @author Senior Developer
 * @since 1.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AsyncProductServiceAdapter implements AsyncProductService {
    
    private final ProductQueryService productQueryService;
    private final ProductServiceClient productServiceClient;
    private final ProductAggregateService productAggregateService;
    private final ProductPriceProviderAdapter priceProviderAdapter;
    
    @Value("${async.product.thread-pool-size:20}")
    private int threadPoolSize;
    
    @Value("${async.product.timeout-seconds:10}")
    private long timeoutSeconds;
    
    // 지연 초기화를 위한 volatile 필드
    private volatile ExecutorService executorService;
    
    /**
     * 스레드풀을 지연 초기화합니다.
     */
    private ExecutorService getExecutorService() {
        if (executorService == null) {
            synchronized (this) {
                if (executorService == null) {
                    executorService = Executors.newFixedThreadPool(threadPoolSize);
                }
            }
        }
        return executorService;
    }
    
    @Override
    public CompletableFuture<ProductQueryService.ProductInfo> getProductAsync(ProductId productId) {
        log.debug("Getting product async: {}", productId);
        
        return CompletableFuture.supplyAsync(
            () -> productQueryService.getProduct(productId),
            getExecutorService()
        ).orTimeout(timeoutSeconds, TimeUnit.SECONDS)
        .exceptionally(throwable -> {
            log.error("Error getting product async: {}", productId, throwable);
            if (throwable.getCause() instanceof ProductNotFoundException) {
                throw (ProductNotFoundException) throwable.getCause();
            }
            throw new ProductServiceException("Async product fetch failed", throwable);
        });
    }
    
    @Override
    public CompletableFuture<Map<ProductId, ProductQueryService.ProductInfo>> getProductsAsync(
            List<ProductId> productIds) {
        
        if (productIds == null || productIds.isEmpty()) {
            return CompletableFuture.completedFuture(new HashMap<>());
        }
        
        log.debug("Getting {} products async", productIds.size());
        
        // 각 상품에 대해 비동기 조회 시작
        Map<ProductId, CompletableFuture<ProductQueryService.ProductInfo>> futures = 
            productIds.stream()
                .distinct()
                .collect(Collectors.toMap(
                    id -> id,
                    this::getProductAsyncQuietly
                ));
        
        // 모든 Future를 하나로 조합
        return CompletableFuture.allOf(
            futures.values().toArray(new CompletableFuture[0])
        ).thenApply(v -> {
            // 성공한 결과만 수집
            Map<ProductId, ProductQueryService.ProductInfo> result = new HashMap<>();
            futures.forEach((id, future) -> {
                try {
                    ProductQueryService.ProductInfo info = future.join();
                    if (info != null) {
                        result.put(id, info);
                    }
                } catch (Exception e) {
                    log.debug("Failed to get product async: {}", id);
                }
            });
            return result;
        }).orTimeout(timeoutSeconds * 2, TimeUnit.SECONDS);  // 배치는 타임아웃을 2배로
    }
    
    @Override
    public CompletableFuture<ProductAggregateService.ProductDetails> getProductDetailsAsync(
            ProductId productId) {
        
        log.debug("Getting product details async: {}", productId);
        
        return CompletableFuture.supplyAsync(
            () -> productAggregateService.getProductWithDetails(productId),
            getExecutorService()
        ).orTimeout(timeoutSeconds, TimeUnit.SECONDS);
    }
    
    @Override
    public CompletableFuture<Integer> getStockAsync(ProductId productId) {
        log.debug("Getting stock async: {}", productId);
        
        return CompletableFuture.supplyAsync(
            () -> productServiceClient.getAvailableStock(productId),
            getExecutorService()
        ).orTimeout(timeoutSeconds / 2, TimeUnit.SECONDS);  // 재고는 빠른 응답 필요
    }
    
    @Override
    public CompletableFuture<ProductAggregateService.PriceInfo> getPriceAsync(ProductId productId) {
        log.debug("Getting price async: {}", productId);
        
        return CompletableFuture.supplyAsync(() -> {
            BigDecimal price = priceProviderAdapter.getPrice(productId);
            
            return new ProductAggregateService.PriceInfo(
                price,
                price,  // 현재는 할인이 없으므로 basePrice = finalPrice
                "KRW",
                List.of(),
                null
            );
        }, getExecutorService())
        .orTimeout(timeoutSeconds / 2, TimeUnit.SECONDS);  // 가격도 빠른 응답 필요
    }
    
    /**
     * 예외를 던지지 않는 비동기 상품 조회.
     * 배치 처리에서 부분 실패를 허용하기 위해 사용됩니다.
     */
    private CompletableFuture<ProductQueryService.ProductInfo> getProductAsyncQuietly(
            ProductId productId) {
        
        return getProductAsync(productId)
            .exceptionally(throwable -> {
                log.debug("Product not found or error during async batch: {}", productId);
                return null;
            });
    }
    
    /**
     * 컴포넌트 종료 시 스레드풀을 정리합니다.
     */
    public void shutdown() {
        if (executorService != null) {
            log.info("Shutting down async product service thread pool");
            executorService.shutdown();
            try {
                if (!executorService.awaitTermination(30, TimeUnit.SECONDS)) {
                    executorService.shutdownNow();
                }
            } catch (InterruptedException e) {
                executorService.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }
}