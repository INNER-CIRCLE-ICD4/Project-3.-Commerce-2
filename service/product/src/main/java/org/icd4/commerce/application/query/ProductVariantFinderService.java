package org.icd4.commerce.application.query;

import lombok.RequiredArgsConstructor;
import org.icd4.commerce.application.provided.ProductVariantFinder;
import org.icd4.commerce.application.required.ProductQueryRepository;
import org.icd4.commerce.domain.product.model.ProductVariant;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ProductVariantFinderService implements ProductVariantFinder {
    private final ProductFinderService productFinderService;
    private final ProductQueryRepository productQueryRepository;

    @Override
    public ProductVariant findVariantBySku(String skuId) {
        return internalFindVariantBySku(skuId);
    }

    @Override
    public ProductVariant findProductVariantByIdAndSku(String productId, String skuId) {
        return productFinderService.findById(productId).findVariantBySku(skuId);
    }

    private ProductVariant internalFindVariantBySku(String skuId) {
        return productQueryRepository.findBySkuReadOnly(skuId)
                .orElseThrow(() -> new IllegalArgumentException("상품 변형을 찾을 수 없습니다. skuId::" + skuId));
    }
}
