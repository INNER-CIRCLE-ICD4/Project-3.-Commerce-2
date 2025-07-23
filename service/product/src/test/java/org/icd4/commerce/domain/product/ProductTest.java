package org.icd4.commerce.domain.product;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProductTest {
    Product product;

    @BeforeEach
    void setUp() {
        product = Product.create(
                new ProductCreateRequest(
                        "sellerId",
                        "0001",
                        "name",
                        "brand",
                        "description",
                        BigDecimal.valueOf(1000),
                        Currency.getInstance(Locale.KOREA).getCurrencyCode(),
                        List.of(new ProductOptionRequest("option1", "value1"),
                                new ProductOptionRequest("option2", "value2"))
                ));
    }

    @Test
    void create() {
        assertThat(product.getStatus()).isEqualTo(ProductStatus.ON_SALE);
        assertThat(product.getCreatedAt()).isNotNull();
    }

    @Test
    void updateInfo() {

    }


    @Test
    void updateInfoCategory() {
        product.changeCategory("002");
        assertThat(product.getCategoryId()).isEqualTo("002");
    }

    //TODO: 도메인 익셉션으로 변경하는 것이 어떨까요?
    @Test
    void alreadyUpdatedCategory() {
        assertThatThrownBy(() -> product.changeCategory("001"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("카테고리 ID가 동일합니다.");

    }

    @Test
    void updateInfoCategoryFail() {
        assertThatThrownBy(() -> product.changeCategory(null))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> product.changeCategory(""))
                .isInstanceOf(IllegalArgumentException.class);
    }

    //TODO: 도메인 익셉션으로 변경하는 것이 어떨까요?
    @Test
    void updateInfoCategoryFailWhenCategoryNotExist() {
        assertThatThrownBy(() -> product.changeCategory("999"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("존재하지 않는 카테고리 ID입니다.");
    }

    @Test
    void changeStatusStopped() {
        product.changeStatusStopped();
        assertThat(product.getStatus()).isEqualTo(ProductStatus.STOPPED);
        assertThat(product.getUpdatedAt()).isNotNull();
    }

    @Test
    void changeStatusFail() {
        product.changeStatusStopped();
        assertThatThrownBy(() -> product.changeStatusStopped())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("이미 판매 중지된 상품입니다.");
    }
}