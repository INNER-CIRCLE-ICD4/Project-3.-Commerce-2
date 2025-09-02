package org.icd4.commerce.adapter.search;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.icd4.commerce.adapter.webapi.dto.event.ProductQueryModel;
import org.icd4.commerce.application.required.ProductSearchClient;
import org.icd4.commerce.domain.product.model.Product;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Slf4j
@Component
@RequiredArgsConstructor
public class SearchClient implements ProductSearchClient {
    private RestClient restClient;
    @Value("${endpoints.search-service.url}")
    private String searchServiceUrl;

    @PostConstruct
    public void init() {
        this.restClient = RestClient.builder()
                .baseUrl(searchServiceUrl)
                .build();
    }

    //TODO 실패 시 재시도, 장애 시 서킷브레이커 등 설정 필요 (동기, 비동기 상관없이)
    @Override
    public String registerProduct(Product product) {
        ProductQueryModel productQuery = ProductQueryModel.fromDomain(product);
//        String query = DataSerializer.serialize(productQuery);
        try {
            // 재고 등록을 위한 요청 객체 생성
            return restClient.post()
                    .uri("/api/v1/product")
                    .body(productQuery)
                    .retrieve()
                    .body(String.class);
        } catch (Exception e) {
            log.error("[SearchClinet.registerProduct()] product: {}", productQuery, e);
        }
        return null;
    }

    @Override
    public String deleteProduct(String productId) {
        try {
            // 재고 등록을 위한 요청 객체 생성
            return restClient.delete()
                    .uri("/api/v1/product/{productId}", productId)
                    .retrieve()
                    .body(String.class);
        } catch (Exception e) {
            log.error("[SearchClinet.deleteProduct()] product: {}", productId, e);
        }
        return null;
    }

    @Override
    public String updateStatus(String productId, String status) {
        try {
            // 재고 등록을 위한 요청 객체 생성
            return restClient.patch()
                    .uri("/api/v1/product/{productId}/status?status={status}", productId, status)
                    .retrieve()
                    .body(String.class);
        } catch (Exception e) {
            log.error("[SearchClinet.deleteProduct()] product: {}", productId, e);
        }
        return null;
    }
}
