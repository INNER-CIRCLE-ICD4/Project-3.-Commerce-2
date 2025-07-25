package org.icd4.commerce.application.query;

import lombok.RequiredArgsConstructor;
import org.icd4.commerce.application.provided.ProductFinder;
import org.icd4.commerce.application.required.ProductQueryRepository;
import org.icd4.commerce.application.required.ProductRepository;
import org.icd4.commerce.domain.product.model.Product;
import org.icd4.commerce.domain.product.model.ProductVariant;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ProductFinderService implements ProductFinder {
    private final ProductQueryRepository productQueryRepository;
    private final ProductRepository productRepository;

    @Override
    public Product findById(String productId) {
        return internalFindById(productId);
    }

    @Override
    public ProductVariant findVariantByProductIdAndSku(String productId, String skuId) {
        return internalFindById(productId).findVariantBySku(skuId);
    }

    @Override
    public ProductVariant findVariantBySku(String skuId) {
        return internalFindVariantBySku(skuId);
    }

    private Product internalFindById(String productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다. productId::" + productId));
    }

    private ProductVariant internalFindVariantBySku(String skuId) {
        return productQueryRepository.findBySkuReadOnly(skuId)
                .orElseThrow(() -> new IllegalArgumentException("상품 변형을 찾을 수 없습니다. skuId::" + skuId));
    }
}
