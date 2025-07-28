package org.icd4.commerce.application.command;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.icd4.commerce.application.provided.ProductFinder;
import org.icd4.commerce.application.provided.ProductModifier;
import org.icd4.commerce.application.required.ProductRepository;
import org.icd4.commerce.domain.product.model.Product;
import org.icd4.commerce.domain.product.model.ProductMoney;
import org.icd4.commerce.domain.product.model.ProductStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.util.Objects;

@RequiredArgsConstructor
@Service
public class ProductModifierService implements ProductModifier {
    private final ProductFinder productFinder;
    private final ProductRepository productRepository;

    //TODO: 초희님 구현
    @Override
    @Transactional
    public Product changeCategory(String productId, String categoryId, String sellerId) {
        Product product = productFinder.findByIdAndSellerId(productId,sellerId);
        product.changeCategory(categoryId);
        return productRepository.save(product);
    }

    //TODO: 초희님 구현
    @Override
    @Transactional // 메모리상의 Product 객체 상태와 일관성 보장하기 위함
    public void activate(String productId, String sellerId) {
        // 필수값 확인 // 존재하는 상품인지 확인 // 판매자 확인
        Product product = productFinder.findByIdAndSellerId(productId, sellerId);
        // 상태 변경
        product.activate();
        productRepository.save(product);
    }

    //TODO: 초희님 구현
    @Override
    @Transactional
    public void inactivate(String productId, String sellerId) {
        Product product = productFinder.findByIdAndSellerId(productId, sellerId);
        product.inactivate();
        productRepository.save(product);
    }

    @Override
    public void changeProductPrice(String productId, String sellerId, ProductMoney newPrice) {
        Product product = productFinder.findByIdAndSellerId(productId, sellerId);
        product.changePrice(newPrice);
        productRepository.save(product);
    }

    @Override
    @Transactional
    public void deleteProduct(String productId, String sellerId) {
        Product product = productFinder.findByIdAndSellerId(productId, sellerId);
        product.delete();
        productRepository.save(product);
    }
}
