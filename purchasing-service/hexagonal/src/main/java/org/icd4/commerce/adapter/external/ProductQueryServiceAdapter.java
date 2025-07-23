package org.icd4.commerce.adapter.external;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.icd4.commerce.domain.cart.ProductId;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * ProductQueryService 구현체.
 * 
 * <p>기존 ProductServiceClient를 래핑하여 향상된 조회 기능을 제공합니다.
 * 배치 조회, Optional 기반 조회 등을 지원합니다.</p>
 * 
 * @author Senior Developer
 * @since 1.0
 */
@Slf4j
@Component
public class ProductQueryServiceAdapter implements ProductQueryService {
    
    private final ProductServiceClient productServiceClient;
    private final ProductServiceRestClient productServiceRestClient;
    
    public ProductQueryServiceAdapter(ProductServiceClient productServiceClient, 
                                     ProductServiceRestClient productServiceRestClient) {
        this.productServiceClient = productServiceClient;
        this.productServiceRestClient = productServiceRestClient;
    }
    
    @Override
    public ProductInfo getProduct(ProductId productId) {
        log.debug("Getting product: {}", productId);
        
        try {
            ProductServiceClient.ProductInfo clientInfo = productServiceClient.getProduct(productId);
            return mapToProductInfo(clientInfo);
        } catch (ProductNotFoundException e) {
            log.warn("Product not found: {}", productId);
            throw e;
        } catch (Exception e) {
            log.error("Error getting product: {}", productId, e);
            throw new ProductServiceException("Failed to get product: " + productId, e);
        }
    }
    
    @Override
    public Map<ProductId, ProductInfo> getProducts(List<ProductId> productIds) {
        if (productIds == null || productIds.isEmpty()) {
            return Collections.emptyMap();
        }
        
        log.debug("Getting {} products in batch", productIds.size());
        
        // 중복 제거
        List<ProductId> uniqueIds = productIds.stream()
            .distinct()
            .toList();
        
        // 청크 단위로 나누어 처리 (한 번에 100개씩)
        Map<ProductId, ProductInfo> result = new HashMap<>();
        int chunkSize = 100;
        
        for (int i = 0; i < uniqueIds.size(); i += chunkSize) {
            List<ProductId> chunk = uniqueIds.subList(i, 
                Math.min(i + chunkSize, uniqueIds.size()));
            
            try {
                Map<ProductId, ProductInfo> chunkResult = fetchProductsBatch(chunk);
                result.putAll(chunkResult);
            } catch (Exception e) {
                log.error("Error fetching batch of products", e);
                // 부분 실패 처리: 개별 조회로 폴백
                Map<ProductId, ProductInfo> fallbackResult = fetchProductsIndividually(chunk);
                result.putAll(fallbackResult);
            }
        }
        
        log.debug("Retrieved {}/{} products successfully", result.size(), uniqueIds.size());
        return result;
    }
    
    @Override
    public Optional<ProductInfo> findProduct(ProductId productId) {
        try {
            return Optional.of(getProduct(productId));
        } catch (ProductNotFoundException e) {
            log.debug("Product not found (returning empty): {}", productId);
            return Optional.empty();
        } catch (Exception e) {
            log.error("Error finding product: {}", productId, e);
            throw new ProductServiceException("Failed to find product: " + productId, e);
        }
    }
    
    /**
     * 배치 API를 통해 상품들을 조회합니다.
     */
    private Map<ProductId, ProductInfo> fetchProductsBatch(List<ProductId> productIds) {
        try {
            if (productServiceRestClient != null) {
                List<ProductServiceClient.ProductInfo> batchResult = 
                    productServiceRestClient.getProductsBatch(productIds);
                
                return batchResult.stream()
                    .collect(Collectors.toMap(
                        info -> ProductId.of(info.id()),
                        this::mapToProductInfo
                    ));
            }
        } catch (Exception e) {
            log.warn("Batch API failed, falling back to individual fetches", e);
        }
        return fetchProductsIndividually(productIds);
    }
    
    /**
     * 상품들을 개별적으로 조회합니다.
     * 실패한 상품은 결과에서 제외됩니다.
     */
    private Map<ProductId, ProductInfo> fetchProductsIndividually(List<ProductId> productIds) {
        Map<ProductId, ProductInfo> result = new HashMap<>();
        
        for (ProductId productId : productIds) {
            try {
                ProductInfo info = getProduct(productId);
                result.put(productId, info);
            } catch (ProductNotFoundException e) {
                log.debug("Product not found during batch fetch: {}", productId);
                // 없는 상품은 결과에서 제외
            } catch (Exception e) {
                log.warn("Error fetching product during batch: {}", productId, e);
                // 조회 실패한 상품도 결과에서 제외
            }
        }
        
        return result;
    }
    
    /**
     * ProductServiceClient.ProductInfo를 ProductQueryService.ProductInfo로 변환합니다.
     */
    private ProductInfo mapToProductInfo(ProductServiceClient.ProductInfo clientInfo) {
        // 재고 상태 판단
        ProductInfo.ProductStatus status;
        if (!clientInfo.isActive()) {
            status = ProductInfo.ProductStatus.STOPPED;
        } else if (clientInfo.availableStock() <= 0) {
            status = ProductInfo.ProductStatus.OUT_OF_STOCK;
        } else {
            status = ProductInfo.ProductStatus.ON_SALE;
        }
        
        return new ProductInfo(
            clientInfo.id(),
            clientInfo.name(),
            null,  // brand는 현재 ProductServiceClient에 없음
            null,  // description도 현재 없음
            clientInfo.price(),
            "KRW",  // 기본 통화
            clientInfo.availableStock(),
            clientInfo.isActive(),
            status
        );
    }
}