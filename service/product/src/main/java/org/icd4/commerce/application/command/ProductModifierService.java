package org.icd4.commerce.application.command;

import lombok.RequiredArgsConstructor;
import org.icd4.commerce.application.provided.ProductFinder;
import org.icd4.commerce.application.provided.ProductModifier;
import org.icd4.commerce.application.required.ProductRepository;
import org.icd4.commerce.domain.product.model.Product;
import org.icd4.commerce.domain.product.model.ProductMoney;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ProductModifierService implements ProductModifier {
    private final ProductFinder productFinder;
    private final ProductRepository productRepository;

    @Override
    @Transactional
    public Product changeCategory(String productId, String categoryId, String sellerId) {
        Product product = productFinder.findByIdAndSellerId(productId,sellerId);
        product.changeCategory(categoryId);
        return productRepository.save(product);
    }

    @Override
    @Transactional
    public Product activate(String productId, String sellerId) {
        Product product = productFinder.findByIdAndSellerId(productId, sellerId);
        product.activate();
        return productRepository.save(product);
    }

    @Override
    @Transactional
    public Product inactivate(String productId, String sellerId) {
        Product product = productFinder.findByIdAndSellerId(productId, sellerId);
        product.inactivate();
        return productRepository.save(product);
    }

    @Override
    public Product changeProductPrice(String productId, String sellerId, ProductMoney newPrice) {
        Product product = productFinder.findByIdAndSellerId(productId, sellerId);
        product.changePrice(newPrice);
        return productRepository.save(product);
    }

    @Override
    @Transactional
    public Product deleteProduct(String productId, String sellerId) {
        Product product = productFinder.findByIdAndSellerId(productId, sellerId);
        product.delete();
        return productRepository.save(product);
    }
}
