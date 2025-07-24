package org.icd4.commerce.application.provided;

import jakarta.persistence.EntityManager;
import org.icd4.commerce.domain.product.model.Product;
import org.icd4.commerce.domain.product.request.ProductCreateRequest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.icd4.commerce.domain.product.model.ProductStatus.ACTIVE;
import static org.icd4.commerce.domain.product.model.ProductStatus.INACTIVE;

@SpringBootTest
@Transactional
class ProductModifierTest {
    ProductModifier productModifier;
    EntityManager entityManager;

    ProductModifierTest(ProductModifier productModifier, EntityManager entityManager) {
        this.productModifier = productModifier;
        this.entityManager = entityManager;
    }

    @Test
    void changeCategory() {
        var categoryId = "0001";
        var sellerId = "0001";

        var product = Product.create(new ProductCreateRequest(
                sellerId,
                "name",
                "brand",
                "description",
                categoryId,
                BigDecimal.ONE,
                "KRW",
                List.of()
        ));

        productModifier.changeCategory(product.getId(), "0002", sellerId);
        entityManager.flush();

        assertThat(product.getCategoryId()).isEqualTo("0002");
    }

    @Test
    void changeCategoryFail() {
        var categoryId = "0001";
        var sellerId = "0001";

        var product = Product.create(new ProductCreateRequest(
                sellerId,
                "name",
                "brand",
                "description",
                categoryId,
                BigDecimal.ONE,
                "KRW",
                List.of()
        ));

        assertThatThrownBy(() -> productModifier.changeCategory(product.getId(), categoryId, sellerId))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> productModifier.changeCategory(product.getId(), null, sellerId))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> productModifier.changeCategory(product.getId(), "", sellerId))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> productModifier.changeCategory("invalid-id", "0002", sellerId))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> productModifier.changeCategory(product.getId(), "0002", "invalid-seller-id"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void productActivate() {
        var categoryId = "0001";
        var sellerId = "0001";

        var product = Product.create(new ProductCreateRequest(
                sellerId,
                "name",
                "brand",
                "description",
                categoryId,
                BigDecimal.ONE,
                "KRW",
                List.of()
        ));

        productModifier.activate();
        entityManager.flush();

        assertThat(product.getStatus()).isEqualTo(ACTIVE);
    }

    @Test
    void productInactivate() {
        var categoryId = "0001";
        var sellerId = "0001";

        var product = Product.create(new ProductCreateRequest(
                sellerId,
                "name",
                "brand",
                "description",
                categoryId,
                BigDecimal.ONE,
                "KRW",
                List.of()
        ));

        productModifier.inactivate();
        entityManager.flush();

        assertThat(product.getStatus()).isEqualTo(INACTIVE);
    }

    @Test
    void changeProductStoppedFail() {
        var categoryId = "0001";
        var sellerId = "0001";

        var product = Product.create(new ProductCreateRequest(
                sellerId,
                "name",
                "brand",
                "description",
                categoryId,
                BigDecimal.ONE,
                "KRW",
                List.of()
        ));

        assertThatThrownBy(() -> productModifier.inactivate())
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> productModifier.inactivate())
                .isInstanceOf(IllegalArgumentException.class);

        product.inactivate();
        entityManager.flush();
        entityManager.clear();

        assertThatThrownBy(() -> product.inactivate())
                .isInstanceOf(IllegalArgumentException.class);

    }
}