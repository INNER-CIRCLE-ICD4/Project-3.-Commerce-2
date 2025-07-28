package org.icd4.commerce.domain.product;

import org.icd4.commerce.domain.product.model.*;
import org.icd4.commerce.domain.product.request.ProductCreateRequest;
import org.icd4.commerce.domain.product.request.ProductInfoUpdateRequest;
import org.icd4.commerce.domain.product.request.ProductVariantRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static java.lang.Thread.sleep;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProductTest {
    ProductCreateRequest productCreateRequest;
    Product product;

    @BeforeEach
    void setUp() {
        productCreateRequest = new ProductCreateRequest(
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
        product = Product.create(productCreateRequest);

    }

    @Nested
    @DisplayName("상품 생성 테스트")
    class CreateProductTest {

        @Test
        @DisplayName("정상적으로 상품이 생성된다")
        void create() {
            assertThat(product.getStatus()).isEqualTo(ProductStatus.ACTIVE);
            assertThat(product.getCreatedAt()).isNotNull();
            assertThat(product.getUpdatedAt()).isNotNull();
            assertThat(product.getIsDeleted()).isFalse();
            assertThat(product.getDeletedAt()).isNull();
            assertThat(product.getSellerId()).isEqualTo("sellerId");
            assertThat(product.getName()).isEqualTo("name");
            assertThat(product.getBrand()).isEqualTo("brand");
            assertThat(product.getDescription()).isEqualTo("description");
            assertThat(product.getCategoryId()).isEqualTo("0001");
            assertThat(product.getBasePrice().getAmount()).isEqualTo(BigDecimal.valueOf(1000));
        }

        @Test
        @DisplayName("필수 값이 null이면 예외가 발생한다")
        void createWithNullValues() {
            assertThatThrownBy(() -> Product.create(
                    new ProductCreateRequest(null, "0001", "name", "brand", "description", BigDecimal.valueOf(1000), "KRW", List.of())))
                    .isInstanceOf(NullPointerException.class);

            assertThatThrownBy(() -> Product.create(
                    new ProductCreateRequest("sellerId", "0001", null, "brand", "description", BigDecimal.valueOf(1000), "KRW", List.of())))
                    .isInstanceOf(NullPointerException.class);
        }
    }


    @Nested
    @DisplayName("상품 정보 수정 테스트")
    class UpdateProductInfoTest {

        @Test
        @DisplayName("상품 정보가 정상적으로 수정된다")
        void updateInfo() throws InterruptedException {
            // given
            ProductInfoUpdateRequest request = new ProductInfoUpdateRequest(
                    "updatedName",
                    "updatedBrand",
                    "updatedDescription",
                    BigDecimal.valueOf(2000),
                    "USD"
            );
            LocalDateTime beforeUpdate = product.getUpdatedAt();

            // when
            sleep(100); // 시간 차이를 주기 위해 잠시 대기
            product.updateInfo(request);

            // then
            assertThat(product.getName()).isEqualTo("updatedName");
            assertThat(product.getBrand()).isEqualTo("updatedBrand");
            assertThat(product.getDescription()).isEqualTo("updatedDescription");
            assertThat(product.getBasePrice().getAmount()).isEqualTo(BigDecimal.valueOf(2000));
            assertThat(product.getBasePrice().getCurrency()).isEqualTo("USD");
            assertThat(product.getUpdatedAt()).isAfter(beforeUpdate);
        }

        @Test
        @DisplayName("수정 시 필수 값이 null이면 예외가 발생한다")
        void updateInfoWithNullValues() {
            assertThatThrownBy(() -> product.updateInfo(
                    new ProductInfoUpdateRequest(null, "brand", "desc", BigDecimal.ONE, "KRW")))
                    .isInstanceOf(NullPointerException.class);
        }
    }

    @Nested
    @DisplayName("카테고리 변경 테스트")
    class ChangeCategoryTest {
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
    }

    @Nested
    @DisplayName("상품 상태 변경 테스트")
    class ChangeStatusTest {
        void changeStatusStopped() {
            product.inactivate();
            assertThat(product.getStatus()).isEqualTo(ProductStatus.INACTIVE);
            assertThat(product.getUpdatedAt()).isNotNull();
        }

        @Test
        void changeStatusFail() {
            product.inactivate();
            assertThatThrownBy(() -> product.inactivate())
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("이미 판매 중지된 상품입니다.");
        }
    }

    @Test
    @DisplayName("상품 상태가 변경되면 모든 변형의 상태도 변경된다")
    void changeStatusAffectsVariants() {
        // given
        Map<String, String> options1 = Map.of("color", "red", "size", "L");
        Map<String, String> options2 = Map.of("color", "blue", "size", "M");

        ProductVariant variant1 = product.addVariant(options1, ProductMoney.of(BigDecimal.valueOf(1500), "KRW"));
        ProductVariant variant2 = product.addVariant(options2, ProductMoney.of(BigDecimal.valueOf(1600), "KRW"));

        variant1.changeStatus(VariantStatus.ACTIVE);
        variant2.changeStatus(VariantStatus.ACTIVE);

        // when
        product.changeStatus(ProductStatus.INACTIVE);

        // then
        assertThat(variant1.getStatus()).isEqualTo(VariantStatus.INACTIVE);
        assertThat(variant2.getStatus()).isEqualTo(VariantStatus.INACTIVE);
    }

    @Nested
    @DisplayName("상품 삭제 테스트")
    class DeleteProductTest {

        @Test
        @DisplayName("상품이 정상적으로 삭제된다")
        void delete() throws InterruptedException {
            // given
            LocalDateTime beforeDelete = product.getUpdatedAt();

            // when
            sleep(100);
            product.delete();

            // then
            assertThat(product.getStatus()).isEqualTo(ProductStatus.INACTIVE);
            assertThat(product.getIsDeleted()).isTrue();
            assertThat(product.getDeletedAt()).isNotNull();
            assertThat(product.getUpdatedAt()).isAfter(beforeDelete);
        }

        @Test
        @DisplayName("상품 삭제 시 모든 변형이 단종 처리된다")
        void deleteAffectsVariants() {
            // given
            Map<String, String> options = Map.of("color", "red");
            ProductVariant variant = product.addVariant(options, ProductMoney.of(BigDecimal.valueOf(1500), "KRW"));
            variant.changeStatus(VariantStatus.ACTIVE);

            // when
            product.delete();

            // then
            assertThat(variant.getStatus()).isEqualTo(VariantStatus.DISCONTINUED);
        }
    }

    @Nested
    @DisplayName("가격 변경 테스트")
    class ChangePriceTest {

        @Test
        @DisplayName("기본 가격이 정상적으로 변경된다")
        void changePrice() {
            // given
            ProductMoney newPrice = ProductMoney.of(BigDecimal.valueOf(2000), "USD");
            LocalDateTime beforeUpdate = product.getUpdatedAt();

            // when
            product.changePrice(newPrice);

            // then
            assertThat(product.getBasePrice()).isEqualTo(newPrice);
            assertThat(product.getUpdatedAt()).isAfter(beforeUpdate);
        }

        @Test
        @DisplayName("null 가격으로 변경 시 예외가 발생한다")
        void changePriceWithNull() {
            assertThatThrownBy(() -> product.changePrice(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("새 가격은 null일 수 없습니다.");
        }
    }

    @Nested
    @DisplayName("상품 변형 관리 테스트")
    class ProductVariantManagementTest {

        @Test
        @DisplayName("상품 변형이 정상적으로 추가된다")
        void addVariant() throws InterruptedException {
            // given
            Map<String, String> options = Map.of("color", "red", "size", "L");
            ProductMoney sellingPrice = ProductMoney.of(BigDecimal.valueOf(1500), "KRW");
            LocalDateTime beforeUpdate = product.getUpdatedAt();

            // when
            sleep(100); // 시간 차이를 주기 위해 잠시 대기
            ProductVariant variant = product.addVariant(options, sellingPrice);

            // then
            assertThat(variant).isNotNull();
            assertThat(variant.getProductId()).isEqualTo(product.getId());
            assertThat(variant.getSellerId()).isEqualTo(product.getSellerId());
            assertThat(variant.getSellingPrice()).isEqualTo(sellingPrice);
            assertThat(variant.getStatus()).isEqualTo(VariantStatus.ACTIVE);
            assertThat(product.getVariants()).hasSize(1);
            assertThat(product.getUpdatedAt()).isAfter(beforeUpdate);
        }

        @Test
        @DisplayName("옵션이 없는 단일 변형도 추가할 수 있다")
        void addVariantWithoutOptions() {
            // given
            Map<String, String> emptyOptions = Collections.emptyMap();
            ProductMoney sellingPrice = ProductMoney.of(BigDecimal.valueOf(1000), "KRW");

            // when
            ProductVariant variant = product.addVariant(emptyOptions, sellingPrice);

            // then
            assertThat(variant.getSku()).isEqualTo(product.getId()); // 옵션이 없으면 productId가 SKU
            assertThat(variant.getOptionCombinationMap()).isEmpty();
        }

        @Test
        @DisplayName("변형을 SKU로 찾을 수 있다")
        void findVariantBySku() {
            // given
            Map<String, String> options = Map.of("color", "red");
            ProductVariant addedVariant = product.addVariant(options, ProductMoney.of(BigDecimal.valueOf(1500), "KRW"));

            // when
            ProductVariant foundVariant = product.findVariantBySku(addedVariant.getSku());

            // then
            assertThat(foundVariant).isNotNull();
            assertThat(foundVariant.getSku()).isEqualTo(addedVariant.getSku());
        }

        @Test
        @DisplayName("존재하지 않는 SKU로 변형을 찾으면 null을 반환한다")
        void findVariantBySkuNotFound() {
            ProductVariant variant = product.findVariantBySku("nonexistent-sku");
            assertThat(variant).isNull();
        }

        @Test
        @DisplayName("변형을 제거할 수 있다")
        void removeVariant() {
            // given
            Map<String, String> options = Map.of("color", "red");
            ProductVariant variant = product.addVariant(options, ProductMoney.of(BigDecimal.valueOf(1500), "KRW"));
            variant.changeStatus(VariantStatus.ACTIVE);

            // when
            product.removeVariant(variant.getSku());

            // then
            assertThat(variant.getStatus()).isEqualTo(VariantStatus.DISCONTINUED);
        }

        @Test
        @DisplayName("존재하지 않는 변형을 제거하려 하면 예외가 발생한다")
        void removeVariantNotFound() {
            assertThatThrownBy(() -> product.removeVariant("nonexistent-sku"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("SKU를 찾을 수 없습니다");
        }

        @Test
        @DisplayName("변형의 가격을 변경할 수 있다")
        void updateVariantPrice() {
            // given
            Map<String, String> options = Map.of("color", "red");
            ProductVariant variant = product.addVariant(options, ProductMoney.of(BigDecimal.valueOf(1500), "KRW"));
            ProductMoney newPrice = ProductMoney.of(BigDecimal.valueOf(2000), "KRW");

            // when
            product.updateVariantPrice(variant.getSku(), newPrice);

            // then
            assertThat(variant.getSellingPrice()).isEqualTo(newPrice);
        }

        @Test
        @DisplayName("변형의 상태를 변경할 수 있다")
        void changeVariantStatus() {
            // given
            Map<String, String> options = Map.of("color", "red");
            ProductVariant variant = product.addVariant(options, ProductMoney.of(BigDecimal.valueOf(1500), "KRW"));

            // when
            product.changeVariantStatus(variant.getSku(), VariantStatus.ACTIVE);

            // then
            assertThat(variant.getStatus()).isEqualTo(VariantStatus.ACTIVE);
        }

        @Test
        @DisplayName("구매 가능한 변형들만 조회할 수 있다")
        void getAvailableVariants() {
            // given
            Map<String, String> options1 = Map.of("color", "red");
            Map<String, String> options2 = Map.of("color", "blue");
            Map<String, String> options3 = Map.of("color", "green");

            ProductVariant variant1 = product.addVariant(options1, ProductMoney.of(BigDecimal.valueOf(1500), "KRW"));
            ProductVariant variant2 = product.addVariant(options2, ProductMoney.of(BigDecimal.valueOf(1600), "KRW"));
            ProductVariant variant3 = product.addVariant(options3, ProductMoney.of(BigDecimal.valueOf(1700), "KRW"));

            variant1.changeStatus(VariantStatus.ACTIVE);
            variant2.changeStatus(VariantStatus.INACTIVE);
            variant3.changeStatus(VariantStatus.ACTIVE);

            // when
            List<ProductVariant> availableVariants = product.getAvailableVariants();

            // then
            assertThat(availableVariants).hasSize(2);
            assertThat(availableVariants).containsExactly(variant1, variant3);
        }

        @Test
        @DisplayName("구매 가능한 변형이 있는지 확인할 수 있다")
        void hasAvailableVariants() {
            // given
            Map<String, String> options = Map.of("color", "red");
            ProductVariant variant = product.addVariant(options, ProductMoney.of(BigDecimal.valueOf(1500), "KRW"));

            // when & then
            assertThat(product.hasAvailableVariants()).isFalse(); // 초기 상태는 REGISTERED

            variant.changeStatus(VariantStatus.ACTIVE);
            assertThat(product.hasAvailableVariants()).isTrue();
        }

        @Test
        @DisplayName("전체 변형 개수를 확인할 수 있다")
        void getVariantCount() {
            assertThat(product.getVariantCount()).isZero();

            product.addVariant(Map.of("color", "red"), ProductMoney.of(BigDecimal.valueOf(1500), "KRW"));
            product.addVariant(Map.of("color", "blue"), ProductMoney.of(BigDecimal.valueOf(1600), "KRW"));

            assertThat(product.getVariantCount()).isEqualTo(2);
        }

        @Test
        @DisplayName("변형이 있는지 확인할 수 있다")
        void hasVariants() {
            assertThat(product.hasVariants()).isFalse();

            product.addVariant(Map.of("color", "red"), ProductMoney.of(BigDecimal.valueOf(1500), "KRW"));

            assertThat(product.hasVariants()).isTrue();
        }
    }


}