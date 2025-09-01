package org.icd4.commerce.application.provided;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import org.icd4.commerce.domain.product.model.Product;
import org.icd4.commerce.domain.product.model.ProductMoney;
import org.icd4.commerce.domain.product.request.ProductCreateRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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

    private Product TEST_PRODUCT;
    private final String SELLER_ID = "seller0001";
    private final String OTHER_SELLER_ID = "seller0002";
    private final BigDecimal PRICE = BigDecimal.ONE;

    ProductModifierTest(ProductModifier productModifier, EntityManager entityManager) {
        this.productModifier = productModifier;
        this.entityManager = entityManager;
    }

    @BeforeEach
    void setUp() {
        TEST_PRODUCT = Product.create(new ProductCreateRequest(
                SELLER_ID,
                "0001",
                "name",
                "brand",
                "description",
                PRICE,
                "KRW",
                List.of()
        ));
        entityManager.persist(TEST_PRODUCT);
        entityManager.flush();
        entityManager.clear();
    }

    @Test
    void changeCategory() {
        productModifier.changeCategory(TEST_PRODUCT.getId(), SELLER_ID, "0002");
        entityManager.flush();

        var product = entityManager.find(Product.class, TEST_PRODUCT.getId());

        assertThat(product.getCategoryId()).isEqualTo("0002");
    }

    @Test
    void changeCategoryFail() {
        assertThatThrownBy(() -> productModifier.changeCategory("invalid-productId", SELLER_ID, "0002"))
                .isInstanceOf(EntityNotFoundException.class);
        assertThatThrownBy(() -> productModifier.changeCategory("", SELLER_ID, "0002"))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> productModifier.changeCategory(null, SELLER_ID, "0002"))
                .isInstanceOf(IllegalArgumentException.class);

        // 기존 데이터 그대로
        assertThatThrownBy(() -> productModifier.changeCategory(TEST_PRODUCT.getId(), SELLER_ID, "0001"))
                .isInstanceOf(IllegalArgumentException.class);
        // 카테고리 null
        assertThatThrownBy(() -> productModifier.changeCategory(TEST_PRODUCT.getId(), SELLER_ID, null))
                .isInstanceOf(IllegalArgumentException.class);
        // 카테고리 빈 문자열
        assertThatThrownBy(() -> productModifier.changeCategory(TEST_PRODUCT.getId(), SELLER_ID, ""))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> productModifier.changeCategory(TEST_PRODUCT.getId(), null, "0002"))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> productModifier.changeCategory(TEST_PRODUCT.getId(), "", "0002"))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> productModifier.changeCategory(TEST_PRODUCT.getId(), OTHER_SELLER_ID, "0002"))
                .isInstanceOf(SecurityException.class);
    }

    @Test
    void productInactivate() {
        productModifier.inactivate(TEST_PRODUCT.getId(), SELLER_ID);
        entityManager.flush();

        var product = entityManager.find(Product.class, TEST_PRODUCT.getId());
        assertThat(product.getStatus()).isEqualTo(INACTIVE);

        productModifier.activate(TEST_PRODUCT.getId(), SELLER_ID);
        entityManager.flush();

        var product2 = entityManager.find(Product.class, TEST_PRODUCT.getId());
        assertThat(product2.getStatus()).isEqualTo(ACTIVE);
    }

    @Test
    @DisplayName("상품이 INACTIVE 상태일 때, 삭제가 된다")
        // inactivate 메서드와 의존성이 생김
    void deleteProduct() {
        Product activateProduct = entityManager.find(Product.class, TEST_PRODUCT.getId());
        productModifier.inactivate(TEST_PRODUCT.getId(), SELLER_ID);
        entityManager.flush();
        entityManager.clear();

        Product inactivateProduct = entityManager.find(Product.class, TEST_PRODUCT.getId());
        assertThat(inactivateProduct.getStatus()).isEqualTo(INACTIVE);

        productModifier.deleteProduct(TEST_PRODUCT.getId(), SELLER_ID);
        entityManager.flush();
        entityManager.clear();

        Product deletedProduct = entityManager.find(Product.class, TEST_PRODUCT.getId());
        assertThat(deletedProduct).isNotNull();
        assertThat(deletedProduct.getIsDeleted()).isTrue();
        assertThat(deletedProduct.getDeletedAt()).isNotNull();
        assertThat(deletedProduct.getStatus()).isEqualTo(INACTIVE);
    }

    @Test
    @DisplayName("상품이 ACTIVE 상태일 떄, 삭제가 실패되고 예외를 발생시킨다 ")
    void deleteProductFailWhenProductIsActive() {
        var activateProduct = entityManager.find(Product.class, TEST_PRODUCT.getId());
        assertThat(activateProduct.getStatus()).isEqualTo(ACTIVE);

        assertThatThrownBy(() -> productModifier.deleteProduct(TEST_PRODUCT.getId(), SELLER_ID))
                .isInstanceOf(IllegalArgumentException.class);
        entityManager.flush();
        entityManager.clear();

        var deletedFailProduct =  entityManager.find(Product.class, TEST_PRODUCT.getId());
        assertThat(deletedFailProduct).isNotNull();
        assertThat(deletedFailProduct.getIsDeleted()).isFalse();
        assertThat(deletedFailProduct.getDeletedAt()).isNull();
    }

    @Test
    @DisplayName("존재하지 않는 상품 ID를 삭제할 때, 삭제가 실패되고 예외를 발생시킨다.")
    void deleteProductFailWhenProductIsNotFound() {
        var nonExistentProductId = "NON-EXISTENT-PRODUCT-ID";

        assertThatThrownBy(() -> productModifier.deleteProduct(nonExistentProductId, SELLER_ID))
                .isInstanceOf(EntityNotFoundException.class);

    }

    @Test
    @DisplayName("권한이 없는 판매자가 삭제를 시도하면, 삭제가 실패되고 예외를 발생시킨다 ")
    void deleteProductFailWhenUnauthorizedSeller() {

        productModifier.inactivate(TEST_PRODUCT.getId(), SELLER_ID);
        entityManager.flush();
        entityManager.clear();

        Product inactiveProduct = entityManager.find(Product.class, TEST_PRODUCT.getId());
        assertThat(inactiveProduct.getStatus()).isEqualTo(INACTIVE);

        assertThatThrownBy(() -> productModifier.deleteProduct(TEST_PRODUCT.getId(), OTHER_SELLER_ID))
                .isInstanceOf(SecurityException.class);
        entityManager.flush();
        // 삭제 실패 된 후 삭제가 안 되었는지 확인
        Product productAfterFail = entityManager.find(Product.class, TEST_PRODUCT.getId());
        assertThat(productAfterFail).isNotNull();
        assertThat(productAfterFail.getDeletedAt()).isNull();
        assertThat(productAfterFail.getIsDeleted()).isFalse();
        assertThat(productAfterFail.getStatus()).isEqualTo(INACTIVE);
    }


    @Test
    @DisplayName("상품 가격이 성공적으로 변경돼야 한다.")
    void changeProductPrice() {

        ProductMoney newPrice = ProductMoney.of(BigDecimal.valueOf(15000), "KRW");

        productModifier.changeProductPrice(TEST_PRODUCT.getId(), SELLER_ID, newPrice);
        entityManager.flush();
        entityManager.clear();

        var updateProduct =  entityManager.find(Product.class, TEST_PRODUCT.getId());
        assertThat(updateProduct.getBasePrice()).isEqualTo(newPrice);
    }

    @Test
    @DisplayName("존재하지 않는 상품의 가격을 변경하려고 할 때, 실패하고 예외를 발생시킨다")
    void changeProductPriceFailWhenProductNotFound() {
        var nonExistentProductId = "nonExistentProductId";
        ProductMoney newPrice = ProductMoney.of(BigDecimal.valueOf(15000), "KRW");

        assertThatThrownBy(() -> productModifier.changeProductPrice(nonExistentProductId, SELLER_ID, newPrice))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("유효하지 않은 (음수 / 0) 가격으로 변경하려고 할 때, 실패하고 예외를 발생시킨다")
    void changeProductPriceFailInvalidPrice() {

        ProductMoney invalidPrice = ProductMoney.of(BigDecimal.valueOf(-15000), "KRW");
        ProductMoney zeroPrice = ProductMoney.of(BigDecimal.valueOf(0), "KRW");

        assertThatThrownBy(() -> productModifier.changeProductPrice(TEST_PRODUCT.getId(), SELLER_ID, invalidPrice))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Price must be a positive value");
        assertThatThrownBy(() -> productModifier.changeProductPrice(TEST_PRODUCT.getId(), SELLER_ID, zeroPrice))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Price must be a positive value");

        assertThat(TEST_PRODUCT.getBasePrice().getAmount()).isEqualTo(PRICE);
    }
}