package org.icd4.commerce.application;

import lombok.RequiredArgsConstructor;
import org.icd4.commerce.application.provided.ProductFinder;
import org.icd4.commerce.application.required.ProductRepository;
import org.icd4.commerce.domain.product.Product;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ProductQueryService implements ProductFinder {
    private final ProductRepository productRepository;

    @Override
    public Product findById(String productId) {
        return productRepository.findById(productId).orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다. productId::" + productId));
    }
}
