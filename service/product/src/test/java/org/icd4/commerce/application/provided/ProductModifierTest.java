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

    ProductModifierTest(ProductModifier productModifier, EntityManager entityManager) {
        this.productModifier = productModifier;
        this.entityManager = entityManager;
    }

    private Product testProduct;
    private final String SELLER_ID = "seller0001";
    private final String OTHER_SELLER_ID = "seller0002";
    private final BigDecimal PRICE = BigDecimal.ONE;
    @BeforeEach
    void setUp() {
        testProduct = Product.create(new ProductCreateRequest(
                SELLER_ID,
                "name",
                "brand",
                "description",
                "0001",
                PRICE,
                "KRW",
                List.of()
        ));
        entityManager.persist(testProduct);
        entityManager.flush();
        entityManager.clear();
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

        productModifier.activate(product.getId(), sellerId);
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

        productModifier.inactivate(product.getId(), sellerId);
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

        assertThatThrownBy(() -> productModifier.inactivate(product.getId(), sellerId))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> productModifier.inactivate(product.getId(), sellerId))
                .isInstanceOf(IllegalArgumentException.class);

        product.inactivate();
        entityManager.flush();
        entityManager.clear();

        assertThatThrownBy(() -> product.inactivate())
                .isInstanceOf(IllegalArgumentException.class);

    }

    @Test
    @DisplayName("상품이 INACTIVE 상태일 때, 삭제가 된다")
    void deleteProduct() {
        Product activateProduct = entityManager.find(Product.class, testProduct.getId());
        productModifier.inactivate(testProduct.getId(), SELLER_ID);
        entityManager.flush();
        entityManager.clear();

        Product inactivateProduct = entityManager.find(Product.class, testProduct.getId());
        assertThat(inactivateProduct.getStatus()).isEqualTo(INACTIVE);

        productModifier.deleteProduct(testProduct.getId(), SELLER_ID);
        entityManager.flush();
        entityManager.clear();

        Product deletedProduct = entityManager.find(Product.class, testProduct.getId());
        assertThat(deletedProduct).isNotNull();
        assertThat(deletedProduct.getIsDeleted()).isTrue();
        assertThat(deletedProduct.getDeletedAt()).isNotNull();
        assertThat(deletedProduct.getStatus()).isEqualTo(INACTIVE);
    }

    @Test
    @DisplayName("상품이 ACTIVE 상태일 떄, 삭제가 실패되고 예외를 발생시킨다 ")
    void deleteProductFailWhenProductIsActive() {
        Product activateProduct = entityManager.find(Product.class, testProduct.getId());
        assertThat(activateProduct.getStatus()).isEqualTo(ACTIVE);

        assertThatThrownBy(() -> productModifier.deleteProduct(testProduct.getId(), SELLER_ID))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Product must be inactive to be deleted");
        entityManager.flush();
        entityManager.clear();

        Product deletedFailProduct = entityManager.find(Product.class, testProduct.getId());
        assertThat(deletedFailProduct).isNotNull();
        assertThat(deletedFailProduct.getIsDeleted()).isFalse();
        assertThat(deletedFailProduct.getDeletedAt()).isNotNull();
        assertThat(deletedFailProduct.getStatus()).isEqualTo(ACTIVE);
    }

    @Test
    @DisplayName("존재하지 않는 상품 ID를 삭제할 때, 삭제가 실패되고 예외를 발생시킨다.")
    void deleteProductFailWhenProductIsNotFound() {
        var nonExistentProductId = "NON-EXISTENT-PRODUCT-ID";

        assertThatThrownBy(() -> productModifier.deleteProduct(nonExistentProductId, SELLER_ID))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Product not found");

    }

    @Test
    @DisplayName("권한이 없는 판매자가 삭제를 시도하면, 삭제가 실패되고 예외를 발생시킨다 ")
    void deleteProductFailWhenUnauthorizedSeller() {

        productModifier.inactivate(testProduct.getId(), SELLER_ID);
        entityManager.flush();
        entityManager.clear();

        Product inactiveProduct = entityManager.find(Product.class, testProduct.getId());
        assertThat(inactiveProduct.getStatus()).isEqualTo(INACTIVE);

        assertThatThrownBy(() -> productModifier.deleteProduct(testProduct.getId(), OTHER_SELLER_ID))
                .isInstanceOf(SecurityException.class)
                .hasMessageContaining("Unauthorized seller");

        entityManager.flush();
        // 삭제 실패 된 후 삭제가 안 되었는지 확인
        Product productAfterFail =  entityManager.find(Product.class, testProduct.getId());
        assertThat(productAfterFail).isNotNull();
        assertThat(productAfterFail.getDeletedAt()).isNotNull();
        assertThat(productAfterFail.getIsDeleted()).isFalse();
        assertThat(productAfterFail.getStatus()).isEqualTo(INACTIVE);
    }


    @Test
    @DisplayName("상품 가격이 성공적으로 변경돼야 한다.")
    void changeProductPrice() {

        ProductMoney newPrice = ProductMoney.of(BigDecimal.valueOf(15000), "KRW");

        productModifier.changeProductPrice(testProduct.getId(), newPrice);
        entityManager.flush();
        entityManager.clear();

        Product updateProduct = entityManager.find(Product.class, testProduct.getId());
        assertThat(updateProduct.getBasePrice()).isEqualTo(newPrice);
    }

    @Test
    @DisplayName("존재하지 않는 상품의 가격을 변경하려고 할 때, 실패하고 예외를 발생시킨다")
    void changeProductPriceFailWhenProductNotFound() {
        var nonExistentProductId = "nonExistentProductId";
        ProductMoney newPrice = ProductMoney.of(BigDecimal.valueOf(15000), "KRW");

        assertThatThrownBy(() -> entityManager.find(Product.class, nonExistentProductId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Product not found with ID ; " + nonExistentProductId);


    }

    @Test
    @DisplayName("유효하지 않은 (음수 / 0) 가격으로 변경하려고 할 때, 실패하고 예외를 발생시킨다")
    void changeProductPriceFailInvalidPrice(){

        ProductMoney invalidPrice = ProductMoney.of(BigDecimal.valueOf(-15000), "KRW");
        ProductMoney zeroPrice = ProductMoney.of(BigDecimal.valueOf(0), "KRW");

        assertThatThrownBy(() -> productModifier.changeProductPrice(testProduct.getId(), invalidPrice))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Price must be a positive value");
        assertThatThrownBy(() -> productModifier.changeProductPrice(testProduct.getId(), zeroPrice))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Price must be a positive value");

        assertThat(testProduct.getBasePrice()).isEqualTo(PRICE);

    }
}