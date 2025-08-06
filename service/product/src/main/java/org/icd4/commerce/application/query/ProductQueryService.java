package org.icd4.commerce.application.query;

import lombok.RequiredArgsConstructor;
import org.icd4.commerce.adapter.webapi.dto.ProductResponse;
import org.icd4.commerce.adapter.webapi.dto.ProductVariantResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ProductQueryService {
    private final ProductFinderService productFinderService;
    private final ProductCacheService productCacheService;
    private final ProductVariantFinderService productVariantFinderService;

    public ProductResponse findById(String productId) {
        return productCacheService.findByIdFromCache(productId);
    }

    public List<ProductVariantResponse> findAllVariants(String productId) {
        return productFinderService.findById(productId).getAllVariants().stream()
                .map(ProductVariantResponse::fromDomain)
                .toList();
    }

    public ProductVariantResponse findVariantByProductIdAndSku(String productId, String skuId) {
        return ProductVariantResponse.fromDomain(
                productVariantFinderService.findProductVariantByIdAndSku(productId, skuId));
    }


}
