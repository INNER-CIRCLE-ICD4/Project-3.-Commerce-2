package org.icd4.commerce.application.command;

import lombok.RequiredArgsConstructor;
import org.icd4.commerce.application.provided.ProductFinder;
import org.icd4.commerce.application.provided.ProductRegister;
import org.icd4.commerce.application.required.ProductRepository;
import org.icd4.commerce.domain.product.model.Product;
import org.icd4.commerce.domain.product.model.VariantStatus;
import org.icd4.commerce.domain.product.request.ProductCreateRequest;
import org.icd4.commerce.domain.product.request.ProductInfoUpdateRequest;
import org.icd4.commerce.domain.product.request.ProductVariantUpdateRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@RequiredArgsConstructor
@Service
public class ProductRegisterService implements ProductRegister {
    private final ProductFinder productFinder;
    private final ProductRepository productRepository;

    @Override
    public Product create(ProductCreateRequest request) {
        Product product = Product.create(request);
        Product savedProduct = productRepository.save(product);
        savedProduct.addVariants(request.variants());
        return savedProduct;
    }

    @Override
    public Product updateInfo(String productId, String sellerId, ProductInfoUpdateRequest request) {
        Product product = productFinder.findByIdAndSellerId(productId, sellerId);
        product.updateInfo(request);
        return productRepository.save(product);
    }

    @Override
    public Product updateVariant(String productId, String sellerId, String sku, ProductVariantUpdateRequest request) {
        Product product = productFinder.findProductWithVariantsByIdAndSellerId(productId, sellerId);
        product.updateVariant(sku, request);
        return productRepository.save(product);
    }

    @Override
    public Product updateVariantStatus(String productId, String sellerId, String sku, VariantStatus status) {
        Product product = productFinder.findProductWithVariantsByIdAndSellerId(productId, sellerId);
        product.updateVariantStatus(sku, status);
        return productRepository.save(product);
    }
}
