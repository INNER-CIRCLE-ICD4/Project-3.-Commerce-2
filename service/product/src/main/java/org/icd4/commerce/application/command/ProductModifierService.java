package org.icd4.commerce.application.command;

import lombok.RequiredArgsConstructor;
import org.icd4.commerce.application.provided.ProductModifier;
import org.icd4.commerce.application.required.ProductRepository;
import org.icd4.commerce.domain.product.Product;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ProductModifierService implements ProductModifier {
    private final ProductRepository productRepository;

    @Override
    public Product changeCategory(String productId, String categoryId, String sellerId) {
        return null;
    }

    @Override
    public Product changeProductStopped(String productId, String sellerId) {
        return null;
    }
}
