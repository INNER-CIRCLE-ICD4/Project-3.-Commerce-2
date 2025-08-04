package org.icd4.commerce.adapter.webapi.dto;

import org.icd4.commerce.domain.Stock;
import org.icd4.commerce.domain.StockStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class StockResponseTest {

    @Test
    @DisplayName("Stock 엔티티로부터 StockResponse 생성")
    void createFromStock() {
        // Given
        Stock stock = Stock.register("PRODUCT-001", 100L);

        // When
        StockResponse response = StockResponse.from(stock);

        // Then
        assertThat(response.getStockId()).isEqualTo(stock.getId());
        assertThat(response.getProductId()).isEqualTo("PRODUCT-001");
        assertThat(response.getQuantity()).isEqualTo(100L);
        assertThat(response.getStockStatus()).isEqualTo(StockStatus.AVAILABLE);
        assertThat(response.getCreatedAt()).isEqualTo(stock.getCreatedAt());
        assertThat(response.getUpdatedAt()).isEqualTo(stock.getUpdatedAt());
    }

    @Test
    @DisplayName("Builder 패턴으로 StockResponse 생성")
    void createWithBuilder() {
        // Given
        String stockId = "test-stock-id";
        String productId = "PRODUCT-002";
        Long quantity = 50L;
        StockStatus status = StockStatus.AVAILABLE;
        LocalDateTime now = LocalDateTime.now();

        // When
        StockResponse response = StockResponse.builder()
                .stockId(stockId)
                .productId(productId)
                .quantity(quantity)
                .stockStatus(status)
                .createdAt(now)
                .updatedAt(now)
                .build();

        // Then
        assertThat(response.getStockId()).isEqualTo(stockId);
        assertThat(response.getProductId()).isEqualTo(productId);
        assertThat(response.getQuantity()).isEqualTo(quantity);
        assertThat(response.getStockStatus()).isEqualTo(status);
        assertThat(response.getCreatedAt()).isEqualTo(now);
        assertThat(response.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    @DisplayName("NoArgsConstructor로 객체 생성 가능")
    void createWithNoArgsConstructor() {
        // Given & When
        StockResponse response = new StockResponse();

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getStockId()).isNull();
        assertThat(response.getProductId()).isNull();
        assertThat(response.getQuantity()).isNull();
        assertThat(response.getStockStatus()).isNull();
        assertThat(response.getCreatedAt()).isNull();
        assertThat(response.getUpdatedAt()).isNull();
    }

    @Test
    @DisplayName("AllArgsConstructor로 객체 생성 가능")
    void createWithAllArgsConstructor() {
        // Given
        String stockId = "test-stock-id";
        String productId = "PRODUCT-003";
        Long quantity = 75L;
        StockStatus status = StockStatus.OUT_OF_STOCK;
        LocalDateTime createdAt = LocalDateTime.now().minusDays(1);
        LocalDateTime updatedAt = LocalDateTime.now();

        // When
        StockResponse response = new StockResponse(stockId, productId, quantity, status, createdAt, updatedAt);

        // Then
        assertThat(response.getStockId()).isEqualTo(stockId);
        assertThat(response.getProductId()).isEqualTo(productId);
        assertThat(response.getQuantity()).isEqualTo(quantity);
        assertThat(response.getStockStatus()).isEqualTo(status);
        assertThat(response.getCreatedAt()).isEqualTo(createdAt);
        assertThat(response.getUpdatedAt()).isEqualTo(updatedAt);
    }

    @Test
    @DisplayName("품절 상태의 재고 응답 생성")
    void createOutOfStockResponse() {
        // Given
        Stock stock = Stock.register("PRODUCT-004", 10L);
        stock.empty(); // 재고를 0으로 만들고 상태를 OUT_OF_STOCK으로 변경

        // When
        StockResponse response = StockResponse.from(stock);

        // Then
        assertThat(response.getQuantity()).isEqualTo(0L);
        assertThat(response.getStockStatus()).isEqualTo(StockStatus.OUT_OF_STOCK);
        assertThat(response.getProductId()).isEqualTo("PRODUCT-004");
    }
} 