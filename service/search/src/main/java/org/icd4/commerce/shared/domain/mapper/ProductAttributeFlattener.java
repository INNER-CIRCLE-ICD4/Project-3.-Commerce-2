package org.icd4.commerce.shared.domain.mapper;

import lombok.extern.slf4j.Slf4j;
import org.icd4.commerce.shared.domain.ProductCreateRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ProductAttributeFlattener {

    public Set<String> flatten(List<ProductCreateRequest.ProductVariantDto> variants) {
        if (variants == null || variants.isEmpty()) {
            return Set.of();
        }

        return variants.stream()
                .filter(this::isActiveWithStock)
                .flatMap(variant -> variant.optionCombination().stream())
                .map(option -> option.name() + ":" + option.value())
                .collect(Collectors.toCollection(TreeSet::new));
    }

    private boolean isActiveWithStock(ProductCreateRequest.ProductVariantDto variant) {
        return "ACTIVE".equals(variant.status());
    }
}