package org.icd4.commerce.application.query;

import lombok.RequiredArgsConstructor;
import org.icd4.commerce.adapter.webapi.dto.ProductResponse;
import org.icd4.commerce.application.provided.ProductFinder;
import org.icd4.commerce.application.required.ProductQueryRepository;
import org.icd4.commerce.domain.product.model.Product;
import org.springframework.stereotype.Service;

import java.time.Duration;

@RequiredArgsConstructor
@Service
public class ProductCacheService {
    private final ProductFinder productFinder;
    private final ProductQueryRepository productQueryRepository;

    public ProductResponse findByIdFromCache(String productId) {
        return productQueryRepository.read(productId)
                .orElseGet(() -> fetch(productId));
    }

    private ProductResponse fetch(String productId) {
        Product product = productFinder.findById(productId);
        ProductResponse response = ProductResponse.fromDomain(product);
        productQueryRepository.create(response, Duration.ofMinutes(10));
        return response;
    }
}
