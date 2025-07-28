package org.icd4.commerce.domain.product.request;

import board.common.dataserializer.DataSerializer;
import org.icd4.commerce.domain.product.model.ProductMoney;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Map;

public record ProductVariantRequest(
        String optionCombination,
        BigDecimal price,
        String currency,
        Long stockQuantity
) {
    public Map<String, String> getOptionCombinationMap() {
        if (optionCombination == null || optionCombination.trim().isEmpty()) {
            return Collections.emptyMap();
        }

        try {
            return DataSerializer.deserialize(optionCombination, Map.class);
        } catch (Exception e) {
            throw new IllegalArgumentException("옵션 조합을 올바르게 직렬화할 수 없습니다: " + optionCombination, e);
        }
    }

    public ProductMoney getSellingPrice() {
        return ProductMoney.of(price, currency);
    }
}
