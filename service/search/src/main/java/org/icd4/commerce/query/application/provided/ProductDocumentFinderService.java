package org.icd4.commerce.query.application.provided;

import lombok.RequiredArgsConstructor;
import org.icd4.commerce.query.application.required.ProductRepository;
import org.icd4.commerce.shared.domain.Product;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductDocumentFinderService implements ProductDocumentFinder {
    private final ProductRepository productRepository;

    @Override
    public Product findById(String productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
    }
}
