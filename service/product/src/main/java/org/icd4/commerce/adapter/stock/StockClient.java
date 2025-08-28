package org.icd4.commerce.adapter.stock;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.icd4.commerce.application.required.ProductStockClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class StockClient implements ProductStockClient {
    private RestClient restClient;
    @Value("${endpoints.stock-service.url}")
    private String stockServiceUrl;

    @PostConstruct
    public void init() {
        this.restClient = RestClient.builder()
                .baseUrl(stockServiceUrl)
                .build();
    }

    //TODO 실패 시 재시도, 장애 시 서킷브레이커 등 설정 필요 (동기, 비동기 상관없이)
    @Override
    public String updateStock(String sku, Long quantity) {
        try {
            // 재고 등록을 위한 요청 객체 생성
            StockRegisterRequest request = new StockRegisterRequest(sku, quantity);

            return restClient.post()
                    .uri("/api/stocks")
                    .body(request)
                    .retrieve()
                    .body(String.class);
        } catch (Exception e) {
            log.error("[StockClient.updateStock()] sku={}, quantity={}", sku, quantity, e);
        }
        return null;
    }

    // 요청 DTO 클래스 추가
    @Getter
    public static class StockRegisterRequest {
        private String productId;
        private Long quantity;

        public StockRegisterRequest(String productId, Long quantity) {
            this.productId = productId;
            this.quantity = quantity;
        }
    }

    @Getter
    public static class StockResponse {
        private String stockId;
        private String productId;
        private Long quantity;
        private String stockStatus;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

}
