package org.icd4.commerce.application.provided;

import jakarta.persistence.EntityManager;
import org.icd4.commerce.domain.product.*;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.icd4.commerce.domain.product.ProductStatus.ON_SALE;

@SpringBootTest
@Transactional
class ProductRegisterTest {
    private final ProductRegister productRegister;
    private final EntityManager entityManager;

    ProductRegisterTest(ProductRegister productRegister, EntityManager entityManager) {
        this.productRegister = productRegister;
        this.entityManager = entityManager;
    }

    @Test
    void create() {
        var product = productRegister.create(
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

        entityManager.flush();

        assertThat(product.getId()).isNotNull();
        assertThat(product.getStatus()).isEqualTo(ON_SALE);
    }

    @Test
    void updateInfo() {
        var product = productRegister.create(
                new ProductCreateRequest(
                        "0001",
                        "0001",
                        "name",
                        "brand",
                        "description",
                        BigDecimal.ONE,
                        "KRW",
                        List.of(new ProductOptionRequest("option1", "value1"),
                                new ProductOptionRequest("option2", "value2")
                        )
                ));

        entityManager.flush();
        entityManager.clear();

        Product updatedProduct = productRegister.updateInfo(product.getId(),new ProductInfoUpdateRequest(
                "name",
                "brand",
                "description",
                BigDecimal.ONE,
                "KRW"
        ));

        entityManager.flush();
        entityManager.clear();

        assertThat(updatedProduct.getName()).isEqualTo("name");
        assertThat(updatedProduct.getBrand()).isEqualTo("brand");
        assertThat(updatedProduct.getDescription()).isEqualTo("description");
        assertThat(updatedProduct.getPrice().getAmount()).isEqualTo(BigDecimal.ONE.setScale(2));
        assertThat(updatedProduct.getPrice().getCurrency()).isEqualTo("KRW");
        // 조회가 안되는 이유
        assertThat(updatedProduct.getOptions()).hasSize(2);

    }
}