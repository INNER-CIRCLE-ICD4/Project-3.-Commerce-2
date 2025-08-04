package org.icd4.commerce.application.query;

import lombok.RequiredArgsConstructor;
import org.icd4.commerce.application.provided.ProductVariantFinder;
import org.icd4.commerce.domain.product.model.ProductVariant;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ProductVariantFinderService implements ProductVariantFinder {
    private final ProductFinderService productFinderService;

    @Override
    public ProductVariant findProductVariantByIdAndSku(String productId, String skuId) {
        return productFinderService.findById(productId).findVariantBySku(skuId);
    }

}
