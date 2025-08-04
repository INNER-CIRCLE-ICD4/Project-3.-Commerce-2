package org.icd4.commerce.adapter.external;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.icd4.commerce.domain.cart.ProductId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * REST API를 통한 상품 서비스 클라이언트 구현.
 * 
 * <p>Spring RestClient를 사용하여 외부 상품 서비스와 통신합니다.
 * 캐싱을 통해 네트워크 호출을 최소화하고 성능을 향상시킵니다.</p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ProductServiceRestClient implements ProductServiceClient {
    
    private final RestClient.Builder restClientBuilder;
    
    @Value("${external.product-service.base-url:http://localhost:8081}")
    private String productServiceBaseUrl;
    
    private RestClient getRestClient() {
        return restClientBuilder
            .baseUrl(productServiceBaseUrl)
            .build();
    }
    
    @Override
    public ProductInfo getProduct(ProductId productId) {
        log.debug("Fetching product info from external service: {}", productId);
        
        try {
            ProductResponse response = getRestClient()
                .get()
                .uri("/api/v1/products/{productId}", productId.value())
                .retrieve()
                .body(ProductResponse.class);
            
            if (response == null) {
                throw new ProductNotFoundException(productId);
            }
            
            log.debug("Product fetched successfully: {}", response);
            
            return new ProductInfo(
                response.id(),
                response.name(),
                response.price(),
                response.stock(),
                response.active()
            );
            
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new ProductNotFoundException(productId);
            }
            throw new ProductServiceException(
                "Failed to fetch product: " + productId, e
            );
        } catch (RestClientException e) {
            throw new ProductServiceException(
                "Product service communication error", e
            );
        }
    }
    
    /**
     * 여러 상품을 배치로 조회합니다.
     * 
     * @param productIds 상품 ID 목록
     * @return 상품 정보 목록
     */
    public List<ProductInfo> getProductsBatch(List<ProductId> productIds) {
        log.debug("Fetching {} products in batch", productIds.size());
        
        try {
            // 상품 ID 목록을 문자열로 변환
            List<String> ids = productIds.stream()
                .map(ProductId::value)
                .toList();
            
            BatchProductRequest request = new BatchProductRequest(ids);
            
            BatchProductResponse response = getRestClient()
                .post()
                .uri("/api/v1/products/batch")
                .body(request)
                .retrieve()
                .body(BatchProductResponse.class);
            
            if (response == null || response.products() == null) {
                return List.of();
            }
            
            log.debug("Batch fetch successful: {} products retrieved", response.products().size());
            
            return response.products().stream()
                .map(this::mapToProductInfo)
                .toList();
            
        } catch (HttpClientErrorException e) {
            log.error("Failed to fetch products in batch", e);
            throw new ProductServiceException(
                "Failed to fetch products in batch", e
            );
        } catch (RestClientException e) {
            log.error("Product service communication error during batch fetch", e);
            throw new ProductServiceException(
                "Product service communication error", e
            );
        }
    }
    
    @Override
    public int getAvailableStock(ProductId productId) {
        log.debug("Checking stock for product: {}", productId);
        
        try {
            StockResponse response = getRestClient()
                .get()
                .uri("/api/v1/products/{productId}/stock", productId.value())
                .retrieve()
                .body(StockResponse.class);
            
            if (response == null) {
                throw new ProductNotFoundException(productId);
            }
            
            return response.availableQuantity();
            
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new ProductNotFoundException(productId);
            }
            throw new ProductServiceException(
                "Failed to check stock: " + productId, e
            );
        } catch (RestClientException e) {
            throw new ProductServiceException(
                "Product service communication error", e
            );
        }
    }
    
    /**
     * 상품 서비스 응답 DTO.
     */
    private record ProductResponse(
        String id,
        String name,
        java.math.BigDecimal price,
        int stock,
        boolean active
    ) {}
    
    /**
     * 재고 조회 응답 DTO.
     */
    private record StockResponse(
        String productId,
        int availableQuantity,
        int reservedQuantity
    ) {}
    
    /**
     * 배치 상품 조회 요청 DTO.
     */
    private record BatchProductRequest(
        List<String> productIds
    ) {}
    
    /**
     * 배치 상품 조회 응답 DTO.
     */
    private record BatchProductResponse(
        List<ProductResponse> products,
        List<String> notFoundIds  // 찾지 못한 상품 ID 목록
    ) {}
    
    /**
     * ProductResponse를 ProductInfo로 변환합니다.
     */
    private ProductInfo mapToProductInfo(ProductResponse response) {
        return new ProductInfo(
            response.id(),
            response.name(),
            response.price(),
            response.stock(),
            response.active()
        );
    }
}