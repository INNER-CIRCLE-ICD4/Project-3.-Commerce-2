package org.icd4.commerce.shared.domain.mapper;

import lombok.RequiredArgsConstructor;
import org.icd4.commerce.shared.domain.Product;
import org.icd4.commerce.shared.domain.ProductCreateRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ProductDocumentMapper {
    private final ProductAttributeFlattener attributeFlattener;
    private final AutoCompleteSuggestionGenerator suggestionGenerator;

    public Product toElasticsearchDocument(ProductCreateRequest request) {
        return Product.builder()
                .id(request.productId())
                .sellerId(request.sellerId())
                .name(request.name())
                .brand(request.brand())
                .description(request.description())
                .basePrice(request.basePrice())
                .categoryId(request.categoryId())
                .status(request.status())
                .createdAt(request.createdAt().toString())
                .updatedAt(request.updatedAt().toString())
                .isDeleted(request.isDeleted())
                .productAttributes(attributeFlattener.flatten(request.variants()))
                .autoCompleteSuggestions(suggestionGenerator.generate(request))
                .variants(convertVariants(request.variants()))
                .build();
    }

    private List<Product.ProductVariant> convertVariants(List<ProductCreateRequest.ProductVariantDto> variants) {
        if (variants == null) return List.of();

        return variants.stream()
                .map(this::convertToProductVariant)
                .collect(Collectors.toList());
    }

    private Product.ProductVariant convertToProductVariant(ProductCreateRequest.ProductVariantDto variantDto) {
        return Product.ProductVariant.builder()
                .sku(variantDto.sku())
                .price(variantDto.price())
                .status(variantDto.status())
                .optionCombination(variantDto.optionCombination() != null ?
                        variantDto.optionCombination().stream()
                                .map(option -> new Product.ProductOption(option.name(), option.value()))
                                .collect(Collectors.toList()) : null)
                .build();
    }
}