package org.icd4.commerce.query.adaptor.web.dto;

import org.icd4.commerce.shared.domain.Product;

import java.math.BigDecimal;

// 검색 결과 담아서 사용자에게 전달 dto
public record SearchResultDto(
        String id,
        String sellerId,
        String name,
        String brand,
        BigDecimal price
) {
    public static SearchResultDto of(Product product) {
        return new SearchResultDto(product.getId(), product.getSellerId(), product.getName(), product.getBrand(), product.getBasePrice());
    }
}
