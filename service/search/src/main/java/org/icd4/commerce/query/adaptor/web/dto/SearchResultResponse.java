package org.icd4.commerce.query.adaptor.web.dto;

import org.icd4.commerce.shared.domain.Product;

import java.math.BigDecimal;

// 검색 결과 담아서 사용자에게 전달 dto
public record SearchResultResponse(
        String id,
        String sellerId,
        String name,
        String brand,
        BigDecimal price
) {
    public static SearchResultResponse of(Product product) {
        return new SearchResultResponse(product.getId(), product.getSellerId(), product.getName(), product.getBrand(), product.getBasePrice());
    }
}
