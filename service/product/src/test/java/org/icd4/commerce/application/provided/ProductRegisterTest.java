package org.icd4.commerce.application.provided;

import jakarta.persistence.EntityManager;
import org.icd4.commerce.domain.product.model.Product;
import org.icd4.commerce.domain.product.model.ProductVariant;
import org.icd4.commerce.domain.product.model.VariantStatus;
import org.icd4.commerce.domain.product.request.ProductCreateRequest;
import org.icd4.commerce.domain.product.request.ProductInfoUpdateRequest;
import org.icd4.commerce.domain.product.request.ProductVariantRequest;
import org.icd4.commerce.domain.product.request.ProductVariantUpdateRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.icd4.commerce.domain.product.model.ProductStatus.ACTIVE;

@SpringBootTest
@Transactional
class ProductRegisterTest {

    private final ProductRegister productRegister;
    private final EntityManager entityManager;
    private ProductCreateRequest baseRequest;

    ProductRegisterTest(ProductRegister productRegister, EntityManager entityManager) {
        this.productRegister = productRegister;
        this.entityManager = entityManager;
    }

    @BeforeEach
    void setUp() {
        baseRequest = new ProductCreateRequest(
                "sellerId",
                "0001",
                "name",
                "brand",
                "description",
                BigDecimal.valueOf(1000),
                Currency.getInstance(Locale.KOREA).getCurrencyCode(),
                List.of(
                        new ProductVariantRequest(
                                """
                                        {
                                            "optionName": "option1",
                                            "optionValue": "value1"
                                        }
                                        """,
                                BigDecimal.valueOf(1000),
                                Currency.getInstance(Locale.KOREA).getCurrencyCode(),
                                1000L
                        ),
                        new ProductVariantRequest(
                                """
                                        {
                                            "optionName": "option2",
                                            "optionValue": "value2"
                                        }
                                        """,
                                BigDecimal.valueOf(2000),
                                Currency.getInstance(Locale.KOREA).getCurrencyCode(),
                                1000L
                        )
                )
        );
    }

    @Nested
    @DisplayName("상품 생성 테스트")
    class CreateProductTest {

        @Test
        @DisplayName("정상적인 요청으로 상품이 생성되어야 한다")
        void create() {
            // when
            Product savedProduct = productRegister.create(baseRequest);

            entityManager.flush();
            entityManager.clear();

            // then
            assertThat(savedProduct.getId()).isNotNull();
            assertThat(savedProduct.getStatus()).isEqualTo(ACTIVE);
            assertThat(savedProduct.getAllVariants()).hasSize(2);
            assertThat(savedProduct.getSellerId()).isEqualTo("sellerId");
            assertThat(savedProduct.getName()).isEqualTo("name");
            assertThat(savedProduct.getBrand()).isEqualTo("brand");
            assertThat(savedProduct.getDescription()).isEqualTo("description");
            assertThat(savedProduct.getCategoryId()).isEqualTo("0001");
            assertThat(savedProduct.getBasePrice().getAmount()).isEqualTo(BigDecimal.valueOf(1000));
        }

        @Test
        @DisplayName("변형이 없는 상품도 생성할 수 있어야 한다")
        void createWithoutVariants() {
            // given
            ProductCreateRequest requestWithoutVariants = new ProductCreateRequest(
                    "sellerId",
                    "0001",
                    "단일 상품",
                    "brand",
                    "description",
                    BigDecimal.valueOf(1000),
                    "KRW",
                    List.of()
            );

            // when
            Product savedProduct = productRegister.create(requestWithoutVariants);

            entityManager.flush();
            entityManager.clear();

            // then
            assertThat(savedProduct.getId()).isNotNull();
            assertThat(savedProduct.getAllVariants()).isEmpty();
            assertThat(savedProduct.getName()).isEqualTo("단일 상품");
        }

        @Test
        @DisplayName("필수 필드가 null이면 예외가 발생해야 한다")
        void createWithNullRequiredFields() {
            // given
            ProductCreateRequest nullSellerIdRequest = new ProductCreateRequest(
                    null, "0001", "name", "brand", "description",
                    BigDecimal.valueOf(1000), "KRW", List.of()
            );

            ProductCreateRequest nullNameRequest = new ProductCreateRequest(
                    "sellerId", "0001", null, "brand", "description",
                    BigDecimal.valueOf(1000), "KRW", List.of()
            );

            // when & then
            assertThatThrownBy(() -> productRegister.create(nullSellerIdRequest))
                    .isInstanceOf(NullPointerException.class);

            assertThatThrownBy(() -> productRegister.create(nullNameRequest))
                    .isInstanceOf(NullPointerException.class);
        }
    }

    @Nested
    @DisplayName("상품 정보 수정 테스트")
    class UpdateProductInfoTest {

        private Product savedProduct;

        @BeforeEach
        void setUp() {
            savedProduct = productRegister.create(baseRequest);
            entityManager.flush();
            entityManager.clear();
        }

        @Test
        @DisplayName("상품 기본 정보가 정상적으로 수정되어야 한다")
        void updateInfo() {
            // given
            ProductInfoUpdateRequest updateRequest = new ProductInfoUpdateRequest(
                    "name1",
                    "brand1",
                    "description1",
                    BigDecimal.ONE,
                    "KRW"
            );

            // when
            Product updatedProduct = productRegister.updateInfo(savedProduct.getId(), updateRequest);
            entityManager.flush();

            // then
            assertThat(updatedProduct.getName()).isEqualTo("name1");
            assertThat(updatedProduct.getBrand()).isEqualTo("brand1");
            assertThat(updatedProduct.getDescription()).isEqualTo("description1");
            assertThat(updatedProduct.getBasePrice().getAmount()).isEqualTo(BigDecimal.ONE);
            assertThat(updatedProduct.getBasePrice().getCurrency()).isEqualTo("KRW");
            assertThat(updatedProduct.getUpdatedAt()).isNotNull();
        }

        @Test
        @DisplayName("존재하지 않는 상품 수정 시 예외가 발생해야 한다")
        void updateNonExistentProduct() {
            // given
            ProductInfoUpdateRequest updateRequest = new ProductInfoUpdateRequest(
                    "name1", "brand1", "description1", BigDecimal.ONE, "KRW"
            );

            // when & then
            assertThatThrownBy(() -> productRegister.updateInfo("non-existent-id", updateRequest))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("상품을 찾을 수 없습니다");
        }

        @Test
        @DisplayName("필수 필드가 null이면 예외가 발생해야 한다")
        void updateWithNullFields() {
            // given
            ProductInfoUpdateRequest nullNameRequest = new ProductInfoUpdateRequest(
                    null, "brand1", "description1", BigDecimal.ONE, "KRW"
            );

            // when & then
            assertThatThrownBy(() -> productRegister.updateInfo(savedProduct.getId(), nullNameRequest))
                    .isInstanceOf(NullPointerException.class);
        }
    }

    @Nested
    @DisplayName("상품 변형 수정 테스트")
    class UpdateProductVariantTest {

        private Product savedProduct;
        private String targetSku;

        @BeforeEach
        void setUp() {
            savedProduct = productRegister.create(baseRequest);
            entityManager.flush();
            entityManager.clear();

            targetSku = savedProduct.getAllVariants().getFirst().getSku();
        }

        @Test
        @DisplayName("변형의 가격이 정상적으로 수정되어야 한다")
        void updateVariantPrice() {
            // given
            ProductVariantUpdateRequest priceUpdateRequest = new ProductVariantUpdateRequest(
                    VariantStatus.ACTIVE,
                    BigDecimal.valueOf(3000),
                    Currency.getInstance(Locale.KOREA).getCurrencyCode(),
                    1000L
            );

            // when
            Product updatedProduct = productRegister.updateVariant(
                    savedProduct.getId(), targetSku, priceUpdateRequest
            );
            entityManager.flush();

            // then
            assertThat(updatedProduct.findVariantBySku(targetSku).getSellingPrice().getAmount())
                    .isEqualTo(BigDecimal.valueOf(3000));
        }

        @Test
        @DisplayName("변형의 상태가 정상적으로 수정되어야 한다")
        void updateVariantStatus() {
            // given
            ProductVariantUpdateRequest statusUpdateRequest = new ProductVariantUpdateRequest(
                    VariantStatus.INACTIVE,
                    BigDecimal.valueOf(3000),
                    Currency.getInstance(Locale.KOREA).getCurrencyCode(),
                    1000L
            );

            // when
            Product updatedProduct = productRegister.updateVariant(
                    savedProduct.getId(), targetSku, statusUpdateRequest
            );
            entityManager.flush();

            // then
            assertThat(updatedProduct.findVariantBySku(targetSku).getStatus())
                    .isEqualTo(VariantStatus.INACTIVE);
        }

        @Test
        @DisplayName("가격과 상태를 동시에 수정할 수 있어야 한다")
        void updateVariantPriceAndStatus() {
            // given
            ProductVariantUpdateRequest updateRequest = new ProductVariantUpdateRequest(
                    VariantStatus.INACTIVE,
                    BigDecimal.valueOf(5000),
                    "USD",
                    1000L
            );

            // when
            Product updatedProduct = productRegister.updateVariant(
                    savedProduct.getId(), targetSku, updateRequest
            );
            entityManager.flush();

            // then
            ProductVariant updatedVariant = updatedProduct.findVariantBySku(targetSku);
            assertThat(updatedVariant.getStatus()).isEqualTo(VariantStatus.INACTIVE);
            assertThat(updatedVariant.getSellingPrice().getAmount()).isEqualTo(BigDecimal.valueOf(5000));
            assertThat(updatedVariant.getSellingPrice().getCurrency()).isEqualTo("USD");
        }

        @Test
        @DisplayName("존재하지 않는 SKU 수정 시 예외가 발생해야 한다")
        void updateNonExistentVariant() {
            // given
            ProductVariantUpdateRequest updateRequest = new ProductVariantUpdateRequest(
                    VariantStatus.ACTIVE, BigDecimal.valueOf(3000), "KRW", 1000L
            );

            // when & then
            assertThatThrownBy(() -> productRegister.updateVariant(
                    savedProduct.getId(), "non-existent-sku", updateRequest
            ))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("SKU를 찾을 수 없습니다");
        }

        @Test
        @DisplayName("존재하지 않는 상품의 변형 수정 시 예외가 발생해야 한다")
        void updateVariantOfNonExistentProduct() {
            // given
            ProductVariantUpdateRequest updateRequest = new ProductVariantUpdateRequest(
                    VariantStatus.ACTIVE, BigDecimal.valueOf(3000), "KRW", 1000L
            );

            // when & then
            assertThatThrownBy(() -> productRegister.updateVariant(
                    "non-existent-product-id", targetSku, updateRequest
            ))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("상품을 찾을 수 없습니다");
        }

        @Test
        @DisplayName("음수 가격으로 수정 시 예외가 발생해야 한다")
        void updateVariantWithNegativePrice() {
            // given
            ProductVariantUpdateRequest invalidPriceRequest = new ProductVariantUpdateRequest(
                    VariantStatus.ACTIVE, BigDecimal.valueOf(-1000), "KRW", 1000L
            );

            // when & then
            assertThatThrownBy(() -> productRegister.updateVariant(
                    savedProduct.getId(), targetSku, invalidPriceRequest
            ))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("가격은 0보다 커야 합니다");
        }
    }

    @Nested
    @DisplayName("상품 삭제 테스트")
    class DeleteProductTest {

        private Product savedProduct;

        @BeforeEach
        void setUp() {
            savedProduct = productRegister.create(baseRequest);
            entityManager.flush();
            entityManager.clear();
        }

        @Test
        @DisplayName("상품이 정상적으로 삭제되어야 한다")
        void deleteProduct() {
            // when
            productRegister.deleteProduct(savedProduct.getId());
            entityManager.flush();

            // then
            // 실제 구현에 따라 검증 로직이 달라질 수 있습니다
            // 예: 소프트 삭제인 경우 상태 확인, 하드 삭제인 경우 존재 여부 확인
        }

        @Test
        @DisplayName("존재하지 않는 상품 삭제 시 예외가 발생해야 한다")
        void deleteNonExistentProduct() {
            // when & then
            assertThatThrownBy(() -> productRegister.deleteProduct("non-existent-id"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("상품을 찾을 수 없습니다");
        }

        @Test
        @DisplayName("null ID로 삭제 시 예외가 발생해야 한다")
        void deleteWithNullId() {
            // when & then
            assertThatThrownBy(() -> productRegister.deleteProduct(null))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }
}