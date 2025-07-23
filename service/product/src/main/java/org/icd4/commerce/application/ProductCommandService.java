package org.icd4.commerce.application;

import org.icd4.commerce.application.command.ProductCreationCommand;
import org.icd4.commerce.application.required.ProductRepository;
import org.icd4.commerce.domain.product.Product;
import org.icd4.commerce.domain.product.ProductMoney;

import java.math.BigDecimal;

public class ProductCommandService {
    private final ProductRepository productRepository;

    public ProductCommandService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public String createProduct(ProductCreationCommand command) {
        Product product = Product.create(command);
        Product savedProduct = productRepository.save(product); // save 시 ID가 채워진 Product 반환
        return savedProduct.getId();
    }

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

    public void changeProductPrice(String productId, ProductMoney newPrice) {
        if (productId == null || productId.isEmpty()) {
            throw new IllegalArgumentException("상품 ID는 필수입니다.");
        }
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다: " + productId));

        product.changePrice(newPrice); // Product 엔티티의 비즈니스 메서드 호출

        productRepository.save(product); // 변경된 엔티티 저장
    }
}
