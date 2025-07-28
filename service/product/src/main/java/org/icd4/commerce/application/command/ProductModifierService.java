package org.icd4.commerce.application.command;

import lombok.RequiredArgsConstructor;
import org.icd4.commerce.application.provided.ProductModifier;
import org.icd4.commerce.application.required.ProductRepository;
import org.icd4.commerce.domain.product.model.Product;
import org.icd4.commerce.domain.product.model.ProductMoney;
import org.icd4.commerce.domain.product.model.ProductStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@RequiredArgsConstructor
@Service
public class ProductModifierService implements ProductModifier {
    private final ProductRepository productRepository;

    //TODO: 초희님 구현
    @Override
    public Product changeCategory(String productId, String categoryId, String sellerId) {
        // 필수 값 들어왔는지 확인
        Objects.requireNonNull(productId, "Product id cannont be null");
        if(productId.isBlank()) { // 공백문자 확인
            throw new IllegalArgumentException("Product id cannot be blank");
        }
        Objects.requireNonNull(categoryId, "Category id cannont be null");
        if(categoryId.isBlank()) {
            throw new IllegalArgumentException("Category id cannot be blank");
        }
        Objects.requireNonNull(sellerId, "Seller id cannont be null");
        if(sellerId.isBlank()) {
            throw new IllegalArgumentException("Seller id cannot be blank");
        }

        // 존재하는 상품인지 확인
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("ID " + productId + "is not exist"));

        // 판매자가 일치하지 않는 경우
        if(!product.getSellerId().equals(sellerId)) {
            throw new  IllegalArgumentException("Seller id " + sellerId + " is not match");
        }

        // ACTIVE 상품만 변경 가능하도록 허용할지? 고민
        if(product.getStatus().equals(ProductStatus.INACTIVE)) {
            throw new IllegalStateException("Cannot change inactive product");
        }

        product.changeCategory(categoryId);
        return productRepository.save(product);
    }

    //TODO: 초희님 구현
    @Override
    @Transactional // 메모리상의 Product 객체 상태와 일관성 보장하기 위함
    public void activate(String productId, String sellerId) {
        // 필수값 확인
        Objects.requireNonNull(productId, "Product id cannont be null");
        if(productId.isBlank()) {
            throw new IllegalArgumentException("Product id cannot be blank");
        }
        Objects.requireNonNull(sellerId, "Seller id cannont be null");
        if(sellerId.isBlank()) {
            throw new IllegalArgumentException("Seller id cannot be blank");
        }

        // 존재하는 상품인지 확인
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product ID: " + productId + " does not exist"));

        // 판매자 확인
        if(!product.getSellerId().equals(sellerId)) {
            throw new  IllegalArgumentException("Seller id " + sellerId + " is not match");
        }

        // 상태 변경
        product.activate();
        productRepository.save(product);
    }

    //TODO: 초희님 구현
    @Override
    @Transactional
    public void inactivate(String productId, String sellerId) {
        // 필수값 체크
        Objects.requireNonNull(productId, "Product id cannont be null");
        if(productId.isBlank()) {
            throw new IllegalArgumentException("Product id cannot be blank");
        }
        Objects.requireNonNull(sellerId, "Seller id cannont be null");
        if(sellerId.isBlank()) {
            throw new IllegalArgumentException("Seller id cannot be blank");
        }

        // 존재하는 상품인지 확인
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product ID : " + productId + " does not exist."));
        // 판매자 일치 확인
        if(!product.getSellerId().equals(sellerId)) {
            throw new  IllegalArgumentException("Seller id " + sellerId + " is not match");
        }
        product.inactivate();
        productRepository.save(product);
    }

    @Override
    public void changeProductPrice(String productId, ProductMoney newPrice) {

    }

    @Override
    public void deleteProduct(String productId, String sellerId) { }


}
