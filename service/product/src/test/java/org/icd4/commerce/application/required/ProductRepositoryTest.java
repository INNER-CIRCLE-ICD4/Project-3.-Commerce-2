package org.icd4.commerce.application.required;

import jakarta.persistence.EntityManager;
import org.icd4.commerce.domain.ProductFixture;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.icd4.commerce.domain.product.model.ProductStatus.ACTIVE;

@DataJpaTest
class ProductRepositoryTest {
    @Autowired
    ProductRepository productRepository;

    @Autowired
    EntityManager entityManager;

    @Test
    void createProduct() {
        var product = ProductFixture.createProduct();
        assertThat(product.getId()).isNull();
        productRepository.save(product);

        assertThat(product.getId()).isNotNull();

        entityManager.flush();
        entityManager.clear();

        var find = productRepository.findById(product.getId()).orElseThrow();
        assertThat(find.getStatus()).isEqualTo(ACTIVE);
        assertThat(find.getCreatedAt()).isNotNull();
    }
}