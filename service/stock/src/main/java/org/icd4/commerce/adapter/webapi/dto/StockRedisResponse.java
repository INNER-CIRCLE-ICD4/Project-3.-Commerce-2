package org.icd4.commerce.adapter.webapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.icd4.commerce.domain.Stock;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockRedisResponse {

    private String stockId;
    private Long quantity;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static StockRedisResponse from(Stock stock) {
        return StockRedisResponse.builder()
                .stockId(stock.getId())
                .quantity(stock.getQuantity())
                .createdAt(stock.getCreatedAt())
                .updatedAt(stock.getUpdatedAt())
                .build();
    }
} 