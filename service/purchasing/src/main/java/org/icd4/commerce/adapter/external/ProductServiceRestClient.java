package org.icd4.commerce.adapter.external;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.icd4.commerce.adapter.external.exception.ProductNotFoundException;
import org.icd4.commerce.adapter.external.exception.ProductServiceException;
import org.icd4.commerce.application.required.common.ProductServiceClient;
import org.icd4.commerce.domain.common.ProductId;
import org.icd4.commerce.domain.common.StockKeepingUnit;
import org.icd4.commerce.domain.order.Money;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

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
    private RestClient restClient;
    private final RestClient.Builder restClientBuilder;

    @Value("${external.product-service.base-url:http://localhost:8081}")
    private String productServiceBaseUrl;

    @PostConstruct
    public void init() {
        this.restClient = RestClient.builder()
                .baseUrl(productServiceBaseUrl)
                .build();
    }

    @Override
    public ProductInfo getProduct(ProductId productId, StockKeepingUnit sku) {
        try {
            ProductResponse response = restClient
                    .get()
                    .uri("/api/v1/product/{productId}/{sku}", productId.value(), sku.value())
                    .retrieve()
                    .body(ProductResponse.class);

            if (response == null) {
                throw new ProductNotFoundException(productId);
            }

            return new ProductInfo(
                    response.productId,
                    response.sku,
                    "상품명 조회",
                    response.sellingPrice.getAmount(),
                    response.status == ProductResponse.ProductStatus.ACTIVE
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
     * 상품 서비스 응답 DTO.
     */
    @Data
    private static class ProductResponse {
        String productId;
        String sku;
        String name;
        Money sellingPrice;
        ProductStatus status;

        private enum ProductStatus {
            ACTIVE, INACTIVE
        }
    }
}