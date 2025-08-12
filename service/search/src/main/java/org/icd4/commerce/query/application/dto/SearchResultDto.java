package org.icd4.commerce.query.application.dto;

import java.math.BigDecimal;

// 검색 결과 담아서 사용자에게 전달 dto
public record SearchResultDto(
        String productId,
        String sellerId,
        String name,
        String brand,

        BigDecimal price
) {
    public static SearchResultDto of(String productId, String sellerId, String name, String brand, BigDecimal price) {
        return new SearchResultDto(productId, sellerId, name, brand, price);
    }
}
