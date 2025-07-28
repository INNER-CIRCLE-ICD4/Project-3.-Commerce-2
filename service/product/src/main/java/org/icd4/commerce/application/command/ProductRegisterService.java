package org.icd4.commerce.application.command;

import lombok.RequiredArgsConstructor;
import org.icd4.commerce.application.provided.ProductFinder;
import org.icd4.commerce.application.provided.ProductRegister;
import org.icd4.commerce.application.required.ProductRepository;
import org.icd4.commerce.domain.product.model.Product;
import org.icd4.commerce.domain.product.model.ProductVariant;
import org.icd4.commerce.domain.product.request.ProductCreateRequest;
import org.icd4.commerce.domain.product.request.ProductInfoUpdateRequest;
import org.icd4.commerce.domain.product.request.ProductVariantRequest;
import org.icd4.commerce.domain.product.request.ProductVariantUpdateRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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
    public Product updateInfo(String productId, ProductInfoUpdateRequest request) {
        Product product = productFinder.findById(productId);
        product.updateInfo(request);
        return productRepository.save(product);
    }

    @Override
    public Product updateVariant(String productId, String sku, ProductVariantUpdateRequest request) {
        Product product = productFinder.findById(productId);
        product.updateVariant(sku, request);
        return productRepository.save(product);
    }

    @Override
    public void deleteProduct(String productId) {
        if (productId == null || productId.isEmpty()) {
            throw new IllegalArgumentException("상품 ID는 필수입니다.");
        }
        if (productRepository.findById(productId).isPresent()) {
            productRepository.deleteById(productId);
        } else {
            throw new IllegalArgumentException("상품을 찾을 수 없습니다: " + productId);
        }
    }
}
