package org.icd4.commerce.shared.domain;

import org.springframework.data.elasticsearch.core.suggest.Completion;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

public record ProductCreateRequest(
        String productId,
        String sellerId,
        String name,
        String brand,
        String description,
        BigDecimal basePrice,
        String categoryId,
        String status,
        ZonedDateTime createdAt,
        ZonedDateTime updatedAt,
        Boolean isDeleted,
        List<String> productAttributes,
        List<String> autoCompleteSuggestions,
        List<ProductVariantDto> variants
) {
    public record ProductVariantDto(
            String sku,
            Long price,
            Integer stock,
            String status,
            List<ProductOptionDto> optionCombination
    ) {
    }

    public record ProductOptionDto(
            String name,
            String value
    ) {
    }

    public Product toProduct() {
        return Product.builder()
                .id(this.productId)
                .sellerId(this.sellerId)
                .name(this.name)
                .brand(this.brand)
                .description(this.description)
                .basePrice(this.basePrice)
                .categoryId(this.categoryId)
                .status(this.status)
                .createdAt(this.createdAt.toString())
                .updatedAt(this.updatedAt.toString())
                .isDeleted(this.isDeleted)
                .productAttributes(this.productAttributes)
                .autoCompleteSuggestions(this.autoCompleteSuggestions != null ?
                        new Completion(autoCompleteSuggestions.toArray(new String[0])) : null)
                .variants(this.variants != null ?
                        this.variants.stream()
                                .map(this::convertToProductVariant)
                                .collect(Collectors.toList()) : null)
                .build();
    }

    private Product.ProductVariant convertToProductVariant(ProductVariantDto variantDto) {
        return Product.ProductVariant.builder()
                .sku(variantDto.sku())
                .price(variantDto.price())
                .stock(variantDto.stock())
                .status(variantDto.status())
                .optionCombination(variantDto.optionCombination() != null ?
                        variantDto.optionCombination().stream()
                                .map(option -> new Product.ProductOption(option.name(), option.value()))
                                .collect(Collectors.toList()) : null)
                .build();
    }
}