package org.icd4.commerce.application.required;

import jakarta.persistence.EntityManager;
import org.icd4.commerce.domain.product.Product;
import org.icd4.commerce.domain.product.ProductCreateRequest;
import org.icd4.commerce.domain.product.ProductOptionRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.icd4.commerce.domain.product.ProductStatus.ON_SALE;

@DataJpaTest
class ProductRepositoryTest {
    @Autowired
    ProductRepository productRepository;

    @Autowired
    EntityManager entityManager;

    @Test
    void createProduct() {
        Product product = Product.create(
                new ProductCreateRequest(
                        "0001",
                        "0001",
                        "name",
                        "brand",
                        "description",
                        BigDecimal.ONE,
                        "KRW",
                        List.of(new ProductOptionRequest("option1", "value1"),
                                new ProductOptionRequest("option2", "value2"))
                )
        );
        assertThat(product.getId()).isNull();
        productRepository.save(product);

        assertThat(product.getId()).isNotNull();

        entityManager.flush();
        entityManager.clear();

        var find = productRepository.findById(product.getId()).orElseThrow();
        assertThat(find.getStatus()).isEqualTo(ON_SALE);
        assertThat(find.getCreatedAt()).isNotNull();
    }
}