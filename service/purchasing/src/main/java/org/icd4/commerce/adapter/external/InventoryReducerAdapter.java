package org.icd4.commerce.adapter.external;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.icd4.commerce.adapter.external.exception.ProductServiceException;
import org.icd4.commerce.application.required.common.InventoryReducer;
import org.icd4.commerce.domain.common.StockKeepingUnit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class InventoryReducerAdapter implements InventoryReducer {
    private RestClient restClient;
    @Value("${external.stock-service.base-url:http://localhost:8082}")
    private String stockServiceBaseUrl;

    @PostConstruct
    public void init() {
        this.restClient = RestClient.builder()
                .baseUrl(stockServiceBaseUrl)
                .build();
    }

    @Override
    public String reduceStock(StockKeepingUnit sku, int quantity) {
        try {
            StockDecreaseRequest request = new StockDecreaseRequest(quantity);
            String response = restClient
                    .patch()
                    .uri("/api/stocks/{stockId}/decrease", sku.value())
                    .body(request)
                    .retrieve()
                    .body(String.class);

            return response;

        } catch (Exception e) {
            throw new ProductServiceException(
                    "Failed to reduce stock: " + sku, e
            );
        }
    }

    private record StockDecreaseRequest(int quantity) {
    }
}
