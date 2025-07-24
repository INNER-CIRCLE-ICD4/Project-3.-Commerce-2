package org.icd4.commerce.application.command;

import lombok.RequiredArgsConstructor;
import org.icd4.commerce.application.provided.ProductModifier;
import org.icd4.commerce.application.required.ProductRepository;
import org.icd4.commerce.domain.product.model.Product;
import org.icd4.commerce.domain.product.model.ProductMoney;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ProductModifierService implements ProductModifier {
    private final ProductRepository productRepository;

    //TODO: 초희님 구현
    @Override
    public Product changeCategory(String productId, String categoryId, String sellerId) {
        return null;
    }

    //TODO: 초희님 구현
    @Override
    public void activate() {

    }

    //TODO: 초희님 구현
    @Override
    public void inactivate() {

    }

    @Override
    public void changeProductPrice(String productId, ProductMoney newPrice) {
        if (productId == null || productId.isEmpty()) {
            throw new IllegalArgumentException("상품 ID는 필수입니다.");
        }
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다: " + productId));

        product.changePrice(newPrice);
        productRepository.save(product);
    }
}
