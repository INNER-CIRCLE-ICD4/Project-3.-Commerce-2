package org.icd4.commerce.application.query;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.icd4.commerce.application.provided.ProductFinder;
import org.icd4.commerce.application.required.ProductQueryRepository;
import org.icd4.commerce.application.required.ProductRepository;
import org.icd4.commerce.domain.product.model.Product;
import org.icd4.commerce.domain.product.model.ProductVariant;
import org.springframework.stereotype.Service;

import java.util.Objects;

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
    public Product findByIdAndSellerId(String productId, String sellerId) {
        validationParameter(productId, sellerId);
        Product product = internalFindById(productId);
        validateSellerPermission(product, sellerId);
        return product;
    }

    @Override
    public ProductVariant findProductVariantByIdAndSku(String productId, String skuId) {
        return internalFindById(productId).findVariantBySku(skuId);
    }

    @Override
    public Product findProductWithVariantsByIdAndSellerId(String productId, String sellerId) {
        validationParameter(productId, sellerId);
        Product product = internalFindProductWithVariant(productId);
        validateSellerPermission(product, sellerId);
        return product;
    }

    @Override
    public ProductVariant findVariantBySku(String skuId) {
        return internalFindVariantBySku(skuId);
    }

    private Product internalFindById(String productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("상품을 찾을 수 없습니다. productId::" + productId));
    }

    private Product internalFindProductWithVariant(String productId) {
        return productRepository.findByIdWithVariants(productId)
                .orElseThrow(() -> new EntityNotFoundException("상품을 찾을 수 없습니다. productId::" + productId));
    }

    private ProductVariant internalFindVariantBySku(String skuId) {
        return productQueryRepository.findBySkuReadOnly(skuId)
                .orElseThrow(() -> new IllegalArgumentException("상품 변형을 찾을 수 없습니다. skuId::" + skuId));
    }

    private void validateSellerPermission(Product product, String sellerId) {
        if (!Objects.equals(product.getSellerId(), sellerId)) {
            throw new SecurityException("Access denied for seller: " + sellerId);
        }
    }

    private void validationParameter(String productId, String sellerId) {
        if (productId == null || productId.trim().isEmpty()) {
            throw new IllegalArgumentException("Seller ID cannot be null or empty");
        }
        if (sellerId == null || sellerId.trim().isEmpty()) {
            throw new IllegalArgumentException("Seller ID cannot be null or empty");
        }
    }
}
