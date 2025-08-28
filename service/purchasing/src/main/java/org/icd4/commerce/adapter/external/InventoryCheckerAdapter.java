package org.icd4.commerce.adapter.external;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.icd4.commerce.adapter.external.exception.ProductNotFoundException;
import org.icd4.commerce.application.required.common.InventoryChecker;
import org.icd4.commerce.application.required.common.ProductServiceClient;
import org.icd4.commerce.domain.common.ProductId;
import org.icd4.commerce.domain.common.StockKeepingUnit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

/**
 * InventoryChecker의 어댑터 구현체.
 * 
 * <p>외부 상품 서비스를 통해 재고를 확인합니다.</p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class InventoryCheckerAdapter implements InventoryChecker {
    private RestClient restClient;
    @Value("${external.stock-service.base-url:http://localhost:8082}")
    private String stockServiceBaseUrl;

    @PostConstruct
    public void init() {
        this.restClient = RestClient.builder()
                .baseUrl(stockServiceBaseUrl)
                .build();
    }

    //TODO 실패 시 재시도, 장애 시 서킷브레이커 등 설정 필요 (동기, 비동기 상관없이)
    @Override
    public AvailableStock getAvailableStock(StockKeepingUnit sku) {
        try {
            StockCheckResponse body = restClient.get()
                    .uri("/api/stocks/{stockId}", sku.value())
                    .retrieve()
                    .body(StockCheckResponse.class);
            assert body != null;
            return new AvailableStock(
                    Integer.parseInt(body.data().get("quantity"))
            );
        } catch (Exception e) {
            log.error("[StockClient.getAvailableStock()] sku={},", sku, e);
        }
        return null;
    }

    private record StockCheckResponse(
            String success,
            Map<String, String> data
    ) {

    }
}