package org.icd4.commerce.query.adaptor.web.dto;

import co.elastic.clients.elasticsearch.core.search.Hit;
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
    public static SearchResultResponse of(Hit<Product> hit) {
        Product product = hit.source();
        return new SearchResultResponse(product.getId(), product.getSellerId(), product.getName(), product.getBrand(), product.getBasePrice());
    }
}
