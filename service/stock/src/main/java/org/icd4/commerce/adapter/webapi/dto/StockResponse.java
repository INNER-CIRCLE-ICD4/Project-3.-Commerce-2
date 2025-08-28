package org.icd4.commerce.adapter.webapi.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.icd4.commerce.domain.Stock;
import org.icd4.commerce.domain.StockStatus;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockResponse {

    private String stockId;
    private String sku;
    private Long quantity;
    private StockStatus stockStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static StockResponse from(Stock stock) {
        return StockResponse.builder()
                .stockId(stock.getId())
                .sku(stock.getSku())
                .quantity(stock.getQuantity())
                .stockStatus(stock.getStockStatus())
                .createdAt(stock.getCreatedAt())
                .updatedAt(stock.getUpdatedAt())
                .build();
    }
}
